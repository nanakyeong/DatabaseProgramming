// 모달 요소
const modal = document.getElementById('readingModal');
const closeBtn = document.querySelector('.modal-close');
const cancelBtn = document.querySelector('.btn-cancel');
const submitBtn = document.querySelector('.btn-submit');
const dateValue = document.querySelector('.date-value');
const textarea = document.querySelector('.form-textarea');
const charCount = document.querySelector('.char-count');
const stars = document.querySelectorAll('.star');

// 캘린더 날짜 클릭 이벤트
document.querySelectorAll('.day:not(.empty)').forEach(day => {
    day.addEventListener('click', function() {
        const date = this.textContent;
        dateValue.textContent = `2025년 7월 ${date}일`;
        modal.classList.add('active');
    });
});

// 모달 닫기
function closeModal() {
    modal.classList.remove('active');
}

closeBtn.addEventListener('click', closeModal);
cancelBtn.addEventListener('click', closeModal);

// 모달 외부 클릭시 닫기
modal.addEventListener('click', function(e) {
    if (e.target === modal) {
        closeModal();
    }
});

// 별점 기능
let selectedRating = 0;

stars.forEach(star => {
    star.addEventListener('click', function() {
        selectedRating = parseInt(this.dataset.rating);
        updateStars();
    });
    
    star.addEventListener('mouseenter', function() {
        const rating = parseInt(this.dataset.rating);
        stars.forEach((s, index) => {
            if (index < rating) {
                s.textContent = '★';
            } else {
                s.textContent = '☆';
            }
        });
    });
});

document.querySelector('.rating-stars').addEventListener('mouseleave', updateStars);

function updateStars() {
    stars.forEach((star, index) => {
        if (index < selectedRating) {
            star.textContent = '★';
            star.classList.add('active');
        } else {
            star.textContent = '☆';
            star.classList.remove('active');
        }
    });
}

// 글자 수 카운트
textarea.addEventListener('input', function() {
    const length = this.value.length;
    charCount.textContent = `${length}/500`;
    
    if (length > 500) {
        this.value = this.value.substring(0, 500);
        charCount.textContent = '500/500';
    }
});

// 저장하기 버튼
submitBtn.addEventListener('click', function() {
    // 여기에 서버로 데이터 전송 로직 추가
    alert('독서 기록이 저장되었습니다!');
    closeModal();
});
