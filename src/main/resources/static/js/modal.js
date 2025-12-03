document.addEventListener('DOMContentLoaded', function() {
    // === 1. 독서 기록 모달 관련 변수 ===
    const readingModal = document.getElementById('readingModal');
    const modalClose = readingModal?.querySelector('.modal-close');
    const btnCancel = readingModal?.querySelector('.btn-cancel');
    const modalFooter = readingModal?.querySelector('.modal-footer');

    // 입력 필드들 (ID로 정확하게 선택)
    const inputTitle = document.getElementById('inputTitle');
    const inputAuthor = document.getElementById('inputAuthor');
    const inputPages = document.getElementById('inputPages');
    const inputStatus = document.getElementById('inputStatus');
    const inputMemo = readingModal.querySelector('.form-textarea');
    const inputCoverUrl = document.getElementById('inputCoverUrl');
    const coverPreview = document.getElementById('coverPreview');
    const previewImg = document.getElementById('previewImg');

    // === 2. 검색 모달 관련 변수 ===
    const searchModal = document.getElementById('searchModal');
    const btnOpenSearch = document.getElementById('btnOpenSearch');
    const btnSearchAction = document.getElementById('btnSearchAction');
    const searchQuery = document.getElementById('searchQuery');
    const searchResultList = document.getElementById('searchResultList');
    const searchClose = searchModal?.querySelector('.modal-close');

    // === 3. 모달 닫기 이벤트 ===
    if (modalClose) modalClose.addEventListener('click', () => closeModal(readingModal));
    if (btnCancel) btnCancel.addEventListener('click', () => closeModal(readingModal));
    if (searchClose) searchClose.addEventListener('click', () => closeModal(searchModal));

    window.addEventListener('click', (e) => {
        if (e.target === readingModal) closeModal(readingModal);
        if (e.target === searchModal) closeModal(searchModal);
    });

    // === 4. 별점 기능 ===
    const stars = document.querySelectorAll('.star');
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = this.dataset.rating;
            this.parentElement.dataset.selectedValue = rating;
            updateStars(rating);
        });
    });

    function updateStars(rating) {
        stars.forEach((s, index) => {
            if (index < rating) {
                s.textContent = '★';
                s.classList.add('active');
            } else {
                s.textContent = '☆';
                s.classList.remove('active');
            }
        });
    }

    // === 5. 글자 수 카운트 ===
    const charCount = readingModal?.querySelector('.char-count');
    if (inputMemo && charCount) {
        inputMemo.addEventListener('input', function() {
            charCount.textContent = `${this.value.length}/500`;
        });
    }

    // === 6. 도서 검색 로직 (추가됨) ===
    if (btnOpenSearch) {
        btnOpenSearch.addEventListener('click', () => {
            openModal(searchModal);
            searchQuery.value = '';
            searchResultList.innerHTML = '<p style="text-align: center; color: #999;">검색어를 입력하세요.</p>';
            searchQuery.focus();
        });
    }

    if (btnSearchAction) {
        btnSearchAction.addEventListener('click', performSearch);
        searchQuery.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') performSearch();
        });
    }

    async function performSearch() {
        const query = searchQuery.value.trim();
        if (!query) return alert('검색어를 입력해주세요.');

        searchResultList.innerHTML = '<p style="text-align: center;">검색 중...</p>';

        try {
            // 백엔드 API 호출 (Controller가 구현되어 있어야 함)
            const res = await fetch(`http://localhost:8080/api/search/books?query=${encodeURIComponent(query)}`);
            const books = await res.json();
            renderSearchResults(books);
        } catch (e) {
            console.error(e);
            searchResultList.innerHTML = '<p style="text-align: center; color: red;">검색 중 오류가 발생했습니다.</p>';
        }
    }

    function renderSearchResults(books) {
        if (!books || books.length === 0) {
            searchResultList.innerHTML = '<p style="text-align: center;">검색 결과가 없습니다.</p>';
            return;
        }

        searchResultList.innerHTML = '';
        const ul = document.createElement('div');
        ul.style.display = 'flex';
        ul.style.flexDirection = 'column';

        books.forEach(book => {
            const item = document.createElement('div');
            item.className = 'search-result-item'; // CSS에 스타일 정의됨

            // 이미지 처리 (없는 경우 대비)
            const imgSrc = book.coverUrl ? book.coverUrl : '';
            const imgHtml = imgSrc ? `<img src="${imgSrc}">` : '<div style="width:50px; height:75px; background:#eee; display:flex; align-items:center; justify-content:center; font-size:10px;">No Img</div>';

            item.innerHTML = `
                ${imgHtml}
                <div>
                    <h4 style="margin: 0 0 5px 0; font-size: 1rem;">${book.title}</h4>
                    <p style="margin: 0; font-size: 0.85rem; color: #666;">${book.author} | ${book.publisher}</p>
                </div>
            `;

            // 책 선택 시 이벤트
            item.addEventListener('click', () => {
                selectBook(book);
            });

            ul.appendChild(item);
        });
        searchResultList.appendChild(ul);
    }

    function selectBook(book) {
        // 검색된 정보를 독서 기록 모달에 채우기
        if (inputTitle) inputTitle.value = book.title;
        if (inputAuthor) inputAuthor.value = book.author;
        if (inputCoverUrl) inputCoverUrl.value = book.coverUrl;

        // 미리보기 표시
        if (book.coverUrl && coverPreview && previewImg) {
            previewImg.src = book.coverUrl;
            coverPreview.style.display = 'block';
        } else {
            coverPreview.style.display = 'none';
        }

        closeModal(searchModal);
    }

    // === 7. 저장/수정/삭제 버튼 동적 생성 및 처리 ===
    window.setupModalButtons = function(mode, dateStr) {
        // 기존 버튼 제거 후 새로 생성
        modalFooter.innerHTML = '<button class="btn-cancel">취소</button>';

        const newCancel = modalFooter.querySelector('.btn-cancel');
        newCancel.addEventListener('click', () => closeModal(readingModal));

        if (mode === 'edit') {
            const btnDelete = document.createElement('button');
            btnDelete.textContent = '삭제';
            btnDelete.className = 'btn-delete';
            btnDelete.style.marginRight = '10px';
            btnDelete.style.backgroundColor = '#ff6b6b';
            btnDelete.style.color = 'white';
            btnDelete.style.border = 'none';
            btnDelete.style.padding = '0.75rem 1.5rem';
            btnDelete.style.borderRadius = '5px';
            btnDelete.style.cursor = 'pointer';

            const btnUpdate = document.createElement('button');
            btnUpdate.textContent = '수정하기';
            btnUpdate.className = 'btn-submit';

            modalFooter.appendChild(btnDelete);
            modalFooter.appendChild(btnUpdate);

            btnDelete.addEventListener('click', () => handleDelete(dateStr));
            btnUpdate.addEventListener('click', () => handleSaveOrUpdate('PUT', dateStr));

        } else {
            const btnSave = document.createElement('button');
            btnSave.textContent = '저장하기';
            btnSave.className = 'btn-submit';
            modalFooter.appendChild(btnSave);

            btnSave.addEventListener('click', () => handleSaveOrUpdate('POST', dateStr));
        }
    };

    // 저장(POST) 또는 수정(PUT) 처리
    async function handleSaveOrUpdate(method, dateStr) {
        const userJson = localStorage.getItem('user');
        if (!userJson) return alert('로그인 필요');
        const user = JSON.parse(userJson);

        // 값 가져오기
        const title = inputTitle.value;
        const author = inputAuthor.value;
        const pages = inputPages.value;
        const status = inputStatus.value;
        const memo = inputMemo.value;
        const rating = parseInt(readingModal.querySelector('.rating-stars').dataset.selectedValue || 0);
        const coverUrl = inputCoverUrl.value; // 표지 URL 추가

        if (!title) return alert('책 제목을 입력하세요.');

        try {
            const response = await fetch('http://localhost:8080/api/reading', {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId: user.id,
                    title, author, pages, status, rating, memo,
                    coverUrl, // 추가됨
                    readingDate: dateStr
                })
            });
            const data = await response.json();
            if (data.success) {
                alert(data.message);
                closeModal(readingModal);
                location.reload();
            } else {
                alert(data.message);
            }
        } catch (e) {
            console.error(e);
            alert('오류 발생');
        }
    }

    // 삭제 처리
    async function handleDelete(dateStr) {
        if (!confirm('정말 삭제하시겠습니까?')) return;

        const userJson = localStorage.getItem('user');
        const user = JSON.parse(userJson);

        try {
            const response = await fetch(`http://localhost:8080/api/reading?userId=${user.id}&date=${dateStr}`, {
                method: 'DELETE'
            });
            const data = await response.json();
            if (data.success) {
                alert('삭제되었습니다.');
                closeModal(readingModal);
                location.reload();
            }
        } catch (e) {
            console.error(e);
            alert('삭제 중 오류 발생');
        }
    }

    // === 8. 모달 데이터 로드 (캘린더 클릭 시) ===
    window.loadModalData = async function(year, month, day) {
        const userJson = localStorage.getItem('user');
        if (!userJson) return;

        const user = JSON.parse(userJson);
        const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

        // 입력 필드 초기화
        inputTitle.value = '';
        inputAuthor.value = '';
        inputPages.value = '';
        inputMemo.value = '';
        inputCoverUrl.value = ''; // 초기화
        coverPreview.style.display = 'none'; // 미리보기 숨김
        updateStars(0);
        readingModal.querySelector('.rating-stars').dataset.selectedValue = 0;

        try {
            const res = await fetch(`http://localhost:8080/api/reading/detail?userId=${user.id}&date=${dateStr}`);
            const result = await res.json();

            if (result.success && result.data) {
                // 데이터가 있으면 폼 채우기 (수정 모드)
                const d = result.data;
                inputTitle.value = d.title;
                inputAuthor.value = d.author;
                inputPages.value = d.pages;
                inputStatus.value = d.status;
                inputMemo.value = d.memo;
                inputCoverUrl.value = d.coverUrl || ''; // URL 불러오기

                // 표지 이미지가 있으면 보여주기
                if (d.coverUrl) {
                    previewImg.src = d.coverUrl;
                    coverPreview.style.display = 'block';
                }

                updateStars(d.rating);
                readingModal.querySelector('.rating-stars').dataset.selectedValue = d.rating;

                window.setupModalButtons('edit', dateStr);
            } else {
                // 데이터가 없으면 (신규 모드)
                window.setupModalButtons('new', dateStr);
            }
        } catch (e) {
            console.error(e);
            window.setupModalButtons('new', dateStr);
        }
    };
});

// 공통 함수
function openModal(modal) {
    if (modal) modal.classList.add('active');
}

function closeModal(modal) {
    if (modal) modal.classList.remove('active');
}