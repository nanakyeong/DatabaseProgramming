document.addEventListener('DOMContentLoaded', function() {
    const readingModal = document.getElementById('readingModal');
    const modalClose = readingModal?.querySelector('.modal-close');
    const btnCancel = readingModal?.querySelector('.btn-cancel');
    // 기존 '저장' 버튼 (초기에는 저장 기능)
    const btnSubmit = readingModal?.querySelector('.btn-submit');

    // 삭제 및 수정 버튼을 담을 영역이 필요하므로, 기존 모달 footer를 제어합니다.
    const modalFooter = readingModal?.querySelector('.modal-footer');

    // 모달 닫기
    if (modalClose) modalClose.addEventListener('click', () => closeModal(readingModal));
    if (btnCancel) btnCancel.addEventListener('click', () => closeModal(readingModal));
    window.addEventListener('click', (e) => {
        if (e.target === readingModal) closeModal(readingModal);
    });

    // 별점 기능
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

    // 글자 수 카운트
    const textarea = readingModal?.querySelector('.form-textarea');
    const charCount = readingModal?.querySelector('.char-count');
    if (textarea && charCount) {
        textarea.addEventListener('input', function() {
            charCount.textContent = `${this.value.length}/500`;
        });
    }

    // 버튼 이벤트 리스너는 openReadingModal 내부 혹은 전역에서 관리
    // 여기서는 동적으로 버튼을 생성/관리하는 함수를 만듭니다.
    window.setupModalButtons = function(mode, dateStr) {
        // 기존 버튼 제거 후 새로 생성 (이벤트 중복 방지)
        modalFooter.innerHTML = '<button class="btn-cancel">취소</button>';

        const newCancel = modalFooter.querySelector('.btn-cancel');
        newCancel.addEventListener('click', () => closeModal(readingModal));

        if (mode === 'edit') {
            // 수정 모드: 삭제 버튼 + 수정 버튼
            const btnDelete = document.createElement('button');
            btnDelete.textContent = '삭제';
            btnDelete.className = 'btn-delete'; // CSS 스타일 필요 (빨간색 등)
            btnDelete.style.marginRight = '10px';
            btnDelete.style.backgroundColor = '#ff6b6b';
            btnDelete.style.color = 'white';
            btnDelete.style.border = 'none';
            btnDelete.style.padding = '0.75rem 1.5rem';
            btnDelete.style.borderRadius = '8px';
            btnDelete.style.cursor = 'pointer';

            const btnUpdate = document.createElement('button');
            btnUpdate.textContent = '수정하기';
            btnUpdate.className = 'btn-submit';

            modalFooter.appendChild(btnDelete);
            modalFooter.appendChild(btnUpdate);

            btnDelete.addEventListener('click', () => handleDelete(dateStr));
            btnUpdate.addEventListener('click', () => handleSaveOrUpdate('PUT', dateStr));

        } else {
            // 신규 모드: 저장 버튼
            const btnSave = document.createElement('button');
            btnSave.textContent = '저장하기';
            btnSave.className = 'btn-submit';
            modalFooter.appendChild(btnSave);

            btnSave.addEventListener('click', () => handleSaveOrUpdate('POST', dateStr));
        }
    };

    // 저장(POST) 또는 수정(PUT) 처리 함수
    async function handleSaveOrUpdate(method, dateStr) {
        const userJson = localStorage.getItem('user');
        if (!userJson) return alert('로그인 필요');
        const user = JSON.parse(userJson);

        const title = readingModal.querySelectorAll('.form-input')[0].value;
        const author = readingModal.querySelectorAll('.form-input')[1].value;
        const pages = readingModal.querySelectorAll('.form-input')[2].value;
        const status = readingModal.querySelector('.form-select').value;
        const memo = readingModal.querySelector('.form-textarea').value;
        const rating = parseInt(readingModal.querySelector('.rating-stars').dataset.selectedValue || 0);

        if (!title) return alert('책 제목을 입력하세요.');

        try {
            const response = await fetch('http://localhost:8080/api/reading', {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId: user.id,
                    title, author, pages, status, rating, memo,
                    readingDate: dateStr // "2025-07-05" 형식
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

    // 삭제 처리 함수
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

    // 전역 함수로 노출 (calendar.js에서 호출)
    window.loadModalData = async function(year, month, day) {
        const userJson = localStorage.getItem('user');
        if (!userJson) return; // 비로그인 시 아무것도 안 함 (저장 시점에 체크)

        const user = JSON.parse(userJson);
        const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

        // 입력 필드 초기화
        const inputs = readingModal.querySelectorAll('.form-input');
        inputs.forEach(i => i.value = '');
        readingModal.querySelector('.form-textarea').value = '';
        updateStars(0);
        readingModal.querySelector('.rating-stars').dataset.selectedValue = 0;

        try {
            // 상세 데이터 조회
            const res = await fetch(`http://localhost:8080/api/reading/detail?userId=${user.id}&date=${dateStr}`);
            const result = await res.json();

            if (result.success && result.data) {
                // 데이터가 있으면 -> 폼 채우기 + 수정/삭제 모드
                const d = result.data;
                inputs[0].value = d.title;
                inputs[1].value = d.author;
                inputs[2].value = d.pages;
                readingModal.querySelector('.form-select').value = d.status;
                readingModal.querySelector('.form-textarea').value = d.memo;
                updateStars(d.rating);
                readingModal.querySelector('.rating-stars').dataset.selectedValue = d.rating;

                window.setupModalButtons('edit', dateStr);
            } else {
                // 데이터가 없으면 -> 신규 저장 모드
                window.setupModalButtons('new', dateStr);
            }
        } catch (e) {
            console.error(e);
            // 에러 시 신규 모드로
            window.setupModalButtons('new', dateStr);
        }
    };
});

function openModal(modal) {
    if (modal) modal.classList.add('active');
}

function closeModal(modal) {
    if (modal) modal.classList.remove('active');
}