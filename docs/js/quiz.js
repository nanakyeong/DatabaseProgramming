document.addEventListener('DOMContentLoaded', function() {
    const quizModal = document.getElementById('quizModal');
    const createQuizModal = document.getElementById('createQuizModal');
    const quizGrid = document.querySelector('.quiz-grid');
    const btnLoadMore = document.getElementById('btnLoadMore'); // 더보기 버튼

    // [추가] 페이징 상태 변수
    let currentPage = 0;
    const pageSize = 6; // 한 번에 불러올 개수 (백엔드 설정과 맞춤)
    let isLastPage = false; // 마지막 페이지 여부

    // 현재 열려있는 퀴즈 ID (댓글/좋아요용)
    let currentQuizIdForComments = null;

    // === 1. 초기 로드 ===
    loadQuizzes(0); // 0페이지 로드
    loadStats();

    // === 2. 퀴즈 목록 불러오기 (페이징 적용) ===
    async function loadQuizzes(page) {
        try {
            // 백엔드가 Page<QuizDto>를 반환하므로 ?page=0&size=6 형태로 호출
            const response = await fetch(`http://localhost:8080/api/quiz?page=${page}&size=${pageSize}`);
            const data = await response.json();
            // 응답 구조: { content: [...], last: boolean, totalPages: int, ... }

            const quizzes = data.content; // 실제 데이터 리스트
            isLastPage = data.last;       // 마지막 페이지 여부

            // 첫 페이지 로딩이라면 그리드 초기화 (기존 로딩 문구 제거)
            if (page === 0) {
                quizGrid.innerHTML = '';
            }

            if (page === 0 && quizzes.length === 0) {
                quizGrid.innerHTML = '<p style="grid-column: 1/-1; text-align: center;">등록된 퀴즈가 없습니다. 첫 퀴즈를 만들어보세요!</p>';
                btnLoadMore.style.display = 'none';
                return;
            }

            // 카드 생성 및 추가 (기존 내용을 유지하면서 뒤에 붙임)
            quizzes.forEach(quiz => {
                const card = document.createElement('div');
                card.className = 'quiz-card';
                let badgeClass = 'medium';
                if(quiz.difficulty === '쉬움') badgeClass = 'easy';
                if(quiz.difficulty === '어려움') badgeClass = 'hard';

                card.innerHTML = `
                    <div class="quiz-card-header">
                        <span class="quiz-badge ${badgeClass}">${quiz.difficulty}</span>
                        <span class="quiz-author">출제자: ${quiz.creatorName}</span>
                    </div>
                    <h3 class="quiz-title">${quiz.bookTitle}</h3>
                    <p class="quiz-meta">${quiz.question}</p>
                    <button class="btn-quiz-start" data-id="${quiz.id}">퀴즈 시작</button>
                `;

                const btn = card.querySelector('.btn-quiz-start');
                btn.addEventListener('click', () => openQuizSolveModal(quiz));

                quizGrid.appendChild(card);
            });

            // 더 보기 버튼 상태 관리
            if (isLastPage) {
                btnLoadMore.style.display = 'none'; // 더 이상 데이터가 없으면 숨김
            } else {
                btnLoadMore.style.display = 'inline-block'; // 데이터가 남았으면 보임
            }

        } catch (error) {
            console.error('퀴즈 목록 로드 실패:', error);
            if (page === 0) {
                quizGrid.innerHTML = '<p style="grid-column: 1/-1; text-align: center;">목록을 불러오지 못했습니다.</p>';
            }
        }
    }

    // [추가] 더 보기 버튼 클릭 이벤트
    if (btnLoadMore) {
        btnLoadMore.addEventListener('click', () => {
            if (!isLastPage) {
                currentPage++; // 페이지 번호 증가
                loadQuizzes(currentPage); // 다음 페이지 로드
            }
        });
    }

    // === 3. 통계 불러오기 ===
    async function loadStats() {
        const userJson = localStorage.getItem('user');
        if (!userJson) return;
        const user = JSON.parse(userJson);

        try {
            const res = await fetch(`http://localhost:8080/api/quiz/stats?userId=${user.id}`);
            if (res.ok) {
                const stats = await res.json();
                const statAttempts = document.getElementById('statAttempts');
                const statAccuracy = document.getElementById('statAccuracy');
                const statPoints = document.getElementById('statPoints');
                const statCreated = document.getElementById('statCreated');

                if (statAttempts) statAttempts.textContent = `${stats.totalAttempts}개`;
                if (statAccuracy) statAccuracy.textContent = `${stats.accuracy}%`;
                if (statPoints) statPoints.textContent = `${stats.points}P`;
                if (statCreated) statCreated.textContent = `${stats.createdCount}개`;

                const headerStat = document.querySelector('.quiz-stat-item strong');
                if(headerStat) headerStat.textContent = `${stats.points}점`;
            }
        } catch (e) {
            console.error("통계 로드 실패:", e);
        }
    }

    // === 4. 퀴즈 풀기 모달 열기 ===
    function openQuizSolveModal(quiz) {
        if (!quizModal) return;

        // UI 업데이트
        quizModal.querySelector('.quiz-book-title').innerHTML = `${quiz.bookTitle} <span class="quiz-difficulty ${quiz.difficulty === '쉬움' ? 'easy' : 'medium'}">${quiz.difficulty}</span>`;
        quizModal.querySelector('.quiz-question-section h3').textContent = quiz.question;

        const optionsContainer = quizModal.querySelector('.quiz-options');
        optionsContainer.innerHTML = `
            ${createOptionHTML('A', quiz.optionA)}
            ${createOptionHTML('B', quiz.optionB)}
            ${createOptionHTML('C', quiz.optionC)}
            ${createOptionHTML('D', quiz.optionD)}
        `;

        // 현재 퀴즈 ID 저장
        quizModal.dataset.currentQuizId = quiz.id;
        currentQuizIdForComments = quiz.id;

        // 좋아요 및 댓글 데이터 로드
        loadLikeStatus(quiz.id);
        loadComments(quiz.id);

        openModal(quizModal);
    }

    function createOptionHTML(label, text) {
        return `
            <div class="quiz-option">
                <input type="radio" name="quiz-answer" id="option-${label}" value="${label}">
                <label for="option-${label}" class="quiz-option-label">
                    <span class="option-letter">${label}</span>
                    <span class="option-text">${text}</span>
                </label>
            </div>
        `;
    }

    // === 5. 정답 제출 (퀴즈 풀기) ===
    const submitQuizBtn = quizModal?.querySelector('.btn-submit-quiz');
    if (submitQuizBtn) {
        submitQuizBtn.addEventListener('click', async function() {
            const userJson = localStorage.getItem('user');
            if (!userJson) return alert('로그인이 필요합니다.');
            const user = JSON.parse(userJson);

            const selected = quizModal.querySelector('input[name="quiz-answer"]:checked');
            if (!selected) return alert('답을 선택해주세요.');

            const quizId = quizModal.dataset.currentQuizId;

            try {
                const response = await fetch('http://localhost:8080/api/quiz/solve', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        userId: user.id,
                        quizId: quizId,
                        selectedAnswer: selected.value
                    })
                });

                const data = await response.json();
                if (data.success) {
                    const result = data.result;
                    if(result.correct) {
                        alert(`${result.message}\n\n[해설] ${result.explanation}`);
                    } else {
                        alert(`${result.message}\n정답은 ${result.correctAnswer}입니다.\n\n[해설] ${result.explanation}`);
                    }
                    closeModal(quizModal);
                    loadStats();
                }
            } catch (e) {
                console.error(e);
                alert('제출 중 오류가 발생했습니다.');
            }
        });
    }

    // === 6. 퀴즈 만들기 (생성) ===
    const createQuizBtns = document.querySelectorAll('[id^="createQuizBtn"]');
    createQuizBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const inputs = createQuizModal.querySelectorAll('input, textarea');
            inputs.forEach(i => i.value = '');
            openModal(createQuizModal);
        });
    });

    const createQuizSubmit = createQuizModal?.querySelector('.btn-submit');
    const createQuizCancel = createQuizModal?.querySelector('.btn-cancel');

    if (createQuizCancel) {
        createQuizCancel.addEventListener('click', () => closeModal(createQuizModal));
    }

    if (createQuizSubmit) {
        createQuizSubmit.addEventListener('click', async function() {
            const userJson = localStorage.getItem('user');
            if (!userJson) return alert('로그인이 필요합니다.');
            const user = JSON.parse(userJson);

            const inputs = createQuizModal.querySelectorAll('.form-input');
            const bookTitle = inputs[0].value;
            const bookAuthor = inputs[1].value;
            const question = inputs[2].value;
            const optionA = inputs[3].value;
            const optionB = inputs[4].value;
            const optionC = inputs[5].value;
            const optionD = inputs[6].value;

            const selects = createQuizModal.querySelectorAll('.form-select');
            const correctAnswer = selects[0].value;
            const difficulty = selects[1].value;
            const explanation = createQuizModal.querySelector('.form-textarea').value;

            if (!bookTitle || !question || !optionA || !optionB) {
                alert('필수 항목을 모두 입력해주세요.');
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/quiz', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        creatorId: user.id,
                        bookTitle, bookAuthor, question,
                        optionA, optionB, optionC, optionD,
                        correctAnswer, difficulty, explanation
                    })
                });

                const data = await response.json();
                if (data.success) {
                    alert('퀴즈가 성공적으로 등록되었습니다!');
                    closeModal(createQuizModal);

                    // 목록 갱신 (첫 페이지부터 다시 로드)
                    currentPage = 0;
                    loadQuizzes(0);
                } else {
                    alert('등록 실패: ' + data.message);
                }
            } catch (e) {
                console.error(e);
                alert('오류가 발생했습니다.');
            }
        });
    }

    // === 7. 좋아요(Like) 로직 ===
    async function loadLikeStatus(quizId) {
        const userJson = localStorage.getItem('user');
        if (!userJson) return;
        const user = JSON.parse(userJson);

        const btnLike = document.getElementById('btnLike');
        const likeCount = document.getElementById('likeCount');

        // 초기화
        btnLike.classList.remove('active');
        likeCount.textContent = '0';

        try {
            const res = await fetch(`http://localhost:8080/api/comments/like?quizId=${quizId}&userId=${user.id}`);
            if (res.ok) {
                const data = await res.json();
                likeCount.textContent = data.count;
                if (data.liked) {
                    btnLike.classList.add('active');
                }
            }
        } catch (e) {
            console.error(e);
        }
    }

    const btnLike = document.getElementById('btnLike');
    if (btnLike) {
        btnLike.addEventListener('click', async function() {
            const userJson = localStorage.getItem('user');
            if (!userJson) return alert('로그인이 필요합니다.');
            const user = JSON.parse(userJson);

            try {
                const res = await fetch('http://localhost:8080/api/comments/like', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ quizId: currentQuizIdForComments, userId: user.id })
                });
                const data = await res.json();

                // UI 업데이트
                const likeCount = document.getElementById('likeCount');
                likeCount.textContent = data.count;
                if (data.liked) {
                    this.classList.add('active');
                } else {
                    this.classList.remove('active');
                }
            } catch (e) {
                console.error('좋아요 처리 실패', e);
            }
        });
    }

    // === 8. 댓글(Comment) 로직 ===
    async function loadComments(quizId) {
        const list = document.getElementById('commentList');
        const countSpan = document.getElementById('commentCount');

        list.innerHTML = '<p style="text-align: center; color: #999;">로딩 중...</p>';

        try {
            const res = await fetch(`http://localhost:8080/api/comments?quizId=${quizId}`);
            const comments = await res.json();

            countSpan.textContent = comments.length;
            list.innerHTML = '';

            if (comments.length === 0) {
                list.innerHTML = '<p style="text-align: center; color: #999; font-size: 0.9rem;">첫 번째 댓글을 남겨보세요!</p>';
                return;
            }

            comments.forEach(c => {
                const div = document.createElement('div');
                div.className = 'comment-item';
                div.innerHTML = `
                    <span class="comment-user">${c.nickname}</span>
                    <span class="comment-content">${c.content}</span>
                    <span class="comment-date">${new Date(c.createdAt).toLocaleDateString()}</span>
                `;
                list.appendChild(div);
            });
        } catch (e) {
            console.error('댓글 로드 실패', e);
            list.innerHTML = '<p style="text-align: center; color: red;">댓글을 불러오지 못했습니다.</p>';
        }
    }

    const btnSubmitComment = document.getElementById('btnSubmitComment');
    if (btnSubmitComment) {
        btnSubmitComment.addEventListener('click', async function() {
            const input = document.getElementById('commentInput');
            const content = input.value.trim();
            if (!content) return alert('내용을 입력해주세요.');

            const userJson = localStorage.getItem('user');
            if (!userJson) return alert('로그인이 필요합니다.');
            const user = JSON.parse(userJson);

            try {
                const res = await fetch('http://localhost:8080/api/comments', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        quizId: currentQuizIdForComments,
                        userId: user.id,
                        content: content
                    })
                });

                if (res.ok) {
                    input.value = '';
                    loadComments(currentQuizIdForComments);
                } else {
                    alert('댓글 등록에 실패했습니다.');
                }
            } catch (e) {
                console.error(e);
                alert('오류가 발생했습니다.');
            }
        });
    }

    // 모달 공통 닫기
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', function() {
            closeModal(quizModal);
            closeModal(createQuizModal);
        });
    });

    window.addEventListener('click', function(e) {
        if (e.target === quizModal) closeModal(quizModal);
        if (e.target === createQuizModal) closeModal(createQuizModal);
    });
});

function openModal(modal) {
    if (modal) modal.classList.add('active');
}

function closeModal(modal) {
    if (modal) modal.classList.remove('active');
}