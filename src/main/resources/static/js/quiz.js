document.addEventListener('DOMContentLoaded', function() {
    const quizModal = document.getElementById('quizModal');
    const createQuizModal = document.getElementById('createQuizModal');
    const quizGrid = document.querySelector('.quiz-grid');

    // === 1. 초기 로드: 퀴즈 목록 및 통계 불러오기 ===
    loadQuizzes();
    loadStats();

    // === 2. 퀴즈 목록 불러오기 및 렌더링 ===
    async function loadQuizzes() {
        try {
            const response = await fetch('http://localhost:8080/api/quiz');
            const quizzes = await response.json();

            // 기존 더미 데이터 비우기
            quizGrid.innerHTML = '';

            if (quizzes.length === 0) {
                quizGrid.innerHTML = '<p style="grid-column: 1/-1; text-align: center;">등록된 퀴즈가 없습니다. 첫 퀴즈를 만들어보세요!</p>';
                return;
            }

            quizzes.forEach(quiz => {
                const card = document.createElement('div');
                card.className = 'quiz-card';
                // 난이도에 따른 클래스 설정
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

                // 동적으로 생성된 버튼에 이벤트 연결 (데이터 전체를 전달)
                const btn = card.querySelector('.btn-quiz-start');
                btn.addEventListener('click', () => openQuizSolveModal(quiz));

                quizGrid.appendChild(card);
            });

        } catch (error) {
            console.error('퀴즈 목록 로드 실패:', error);
        }
    }

    // === 3. 통계 불러오기 ===
    // === 3. 통계 불러오기 ===
    async function loadStats() {
        const userJson = localStorage.getItem('user');
        if (!userJson) return; // 비로그인 시 중단
        const user = JSON.parse(userJson);

        try {
            const res = await fetch(`http://localhost:8080/api/quiz/stats?userId=${user.id}`);
            if (res.ok) {
                const stats = await res.json();

                // HTML 요소 업데이트 (ID 사용)
                const statAttempts = document.getElementById('statAttempts');
                const statAccuracy = document.getElementById('statAccuracy');
                const statPoints = document.getElementById('statPoints');
                const statCreated = document.getElementById('statCreated');

                if (statAttempts) statAttempts.textContent = `${stats.totalAttempts}개`;
                if (statAccuracy) statAccuracy.textContent = `${stats.accuracy}%`;
                if (statPoints) statPoints.textContent = `${stats.points}P`;
                if (statCreated) statCreated.textContent = `${stats.createdCount}개`; // 만든 퀴즈

                // 헤더 부분 통계 (현재 점수)
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

        // 제목, 난이도, 질문 업데이트
        quizModal.querySelector('.quiz-book-title').innerHTML = `${quiz.bookTitle} <span class="quiz-difficulty ${quiz.difficulty === '쉬움' ? 'easy' : 'medium'}">${quiz.difficulty}</span>`;
        quizModal.querySelector('.quiz-question-section h3').textContent = quiz.question;

        // 선택지 업데이트
        const optionsContainer = quizModal.querySelector('.quiz-options');
        optionsContainer.innerHTML = `
            ${createOptionHTML('A', quiz.optionA)}
            ${createOptionHTML('B', quiz.optionB)}
            ${createOptionHTML('C', quiz.optionC)}
            ${createOptionHTML('D', quiz.optionD)}
        `;

        // 현재 풀고 있는 퀴즈 ID 저장 (제출 시 사용)
        quizModal.dataset.currentQuizId = quiz.id;

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
                    // 결과 알림
                    if(result.correct) {
                        alert(`${result.message}\n\n[해설] ${result.explanation}`);
                    } else {
                        alert(`${result.message}\n정답은 ${result.correctAnswer}입니다.\n\n[해설] ${result.explanation}`);
                    }
                    closeModal(quizModal);
                    loadStats(); // 점수 갱신
                }
            } catch (e) {
                console.error(e);
                alert('제출 중 오류가 발생했습니다.');
            }
        });
    }

    // === 6. 퀴즈 만들기 (생성) ===
    // 퀴즈 만들기 버튼들
    const createQuizBtns = document.querySelectorAll('[id^="createQuizBtn"]');
    createQuizBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            // 폼 초기화
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

            // 입력값 가져오기 (HTML 구조에 따라 인덱스 주의)
            const inputs = createQuizModal.querySelectorAll('.form-input');
            // 순서: 0:책제목, 1:저자, 2:질문, 3:옵션A, 4:옵션B, 5:옵션C, 6:옵션D
            const bookTitle = inputs[0].value;
            const bookAuthor = inputs[1].value;
            const question = inputs[2].value;
            const optionA = inputs[3].value;
            const optionB = inputs[4].value;
            const optionC = inputs[5].value;
            const optionD = inputs[6].value;

            // Select 박스들
            const selects = createQuizModal.querySelectorAll('.form-select');
            const correctAnswer = selects[0].value; // 정답 (A, B...)
            const difficulty = selects[1].value;    // 난이도

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
                    loadQuizzes(); // 목록 갱신
                } else {
                    alert('등록 실패: ' + data.message);
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