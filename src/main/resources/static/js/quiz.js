// 모달 요소들
const quizModal = document.getElementById('quizModal');
const createQuizModal = document.getElementById('createQuizModal');
const closeButtons = document.querySelectorAll('.modal-close');
const cancelButtons = document.querySelectorAll('.btn-cancel');

// 퀴즈 시작 버튼들
document.querySelectorAll('.btn-quiz-start').forEach(btn => {
    btn.addEventListener('click', function() {
        quizModal.classList.add('active');
    });
});

// 퀴즈 만들기 버튼들
const createQuizBtn = document.getElementById('createQuizBtn');
const createQuizBtn2 = document.getElementById('createQuizBtn2');

if (createQuizBtn) {
    createQuizBtn.addEventListener('click', function() {
        createQuizModal.classList.add('active');
    });
}

if (createQuizBtn2) {
    createQuizBtn2.addEventListener('click', function() {
        createQuizModal.classList.add('active');
    });
}

// 모달 닫기
function closeModal(modal) {
    modal.classList.remove('active');
}

closeButtons.forEach(btn => {
    btn.addEventListener('click', function() {
        const modal = this.closest('.modal');
        closeModal(modal);
    });
});

cancelButtons.forEach(btn => {
    btn.addEventListener('click', function() {
        const modal = this.closest('.modal');
        closeModal(modal);
    });
});

// 모달 외부 클릭시 닫기
[quizModal, createQuizModal].forEach(modal => {
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeModal(modal);
            }
        });
    }
});

// 퀴즈 정답 제출
const submitQuizBtn = document.querySelector('.btn-submit-quiz');
if (submitQuizBtn) {
    submitQuizBtn.addEventListener('click', function() {
        const selectedOption = document.querySelector('input[name="quiz-answer"]:checked');
        if (!selectedOption) {
            alert('답을 선택해주세요!');
            return;
        }
        
        // 정답 확인 로직 (예시: A가 정답)
        if (selectedOption.value === 'A') {
            alert('정답입니다! 🎉');
        } else {
            alert('오답입니다. 다시 시도해보세요!');
        }
        
        closeModal(quizModal);
    });
}

// 퀴즈 등록하기
const submitCreateQuizBtn = document.querySelector('#createQuizModal .btn-submit');
if (submitCreateQuizBtn) {
    submitCreateQuizBtn.addEventListener('click', function() {
        alert('퀴즈가 등록되었습니다!');
        closeModal(createQuizModal);
    });
}
