// 모달 요소
const loginModal = document.getElementById('loginModal');
const signupModal = document.getElementById('signupModal');
const btnLogin = document.querySelector('.btn-login');
const btnSignup = document.querySelector('.btn-signup');

// 로그인 버튼 클릭
if (btnLogin) {
    btnLogin.addEventListener('click', function() {
        loginModal.classList.add('active');
    });
}

// 회원가입 버튼 클릭
if (btnSignup) {
    btnSignup.addEventListener('click', function() {
        signupModal.classList.add('active');
    });
}

// 모달 닫기 버튼
document.querySelectorAll('[data-modal]').forEach(btn => {
    btn.addEventListener('click', function() {
        const modalType = this.getAttribute('data-modal');
        if (modalType === 'login') {
            loginModal.classList.remove('active');
        } else if (modalType === 'signup') {
            signupModal.classList.remove('active');
        }
    });
});

// 모달 전환: 로그인 → 회원가입
const showSignupFromLogin = document.getElementById('showSignupFromLogin');
if (showSignupFromLogin) {
    showSignupFromLogin.addEventListener('click', function(e) {
        e.preventDefault();
        loginModal.classList.remove('active');
        signupModal.classList.add('active');
    });
}

// 모달 전환: 회원가입 → 로그인
const showLoginFromSignup = document.getElementById('showLoginFromSignup');
if (showLoginFromSignup) {
    showLoginFromSignup.addEventListener('click', function(e) {
        e.preventDefault();
        signupModal.classList.remove('active');
        loginModal.classList.add('active');
    });
}

// 모달 외부 클릭시 닫기
[loginModal, signupModal].forEach(modal => {
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.classList.remove('active');
            }
        });
    }
});

// 로그인 처리
const loginForm = document.querySelector('#loginModal .btn-submit');
if (loginForm) {
    loginForm.addEventListener('click', async function() {
        const email = document.querySelector('#loginModal input[type="email"]').value;
        const password = document.querySelector('#loginModal input[type="password"]').value;
        
        if (!email || !password) {
            alert('이메일과 비밀번호를 입력해주세요.');
            return;
        }
        
        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password })
            });
            
            if (response.ok) {
                const user = await response.json();
                localStorage.setItem('user', JSON.stringify(user));
                alert(`환영합니다, ${user.nickname}님!`);
                loginModal.classList.remove('active');
                location.reload();
            } else {
                const error = await response.text();
                alert(error || '로그인에 실패했습니다.');
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('로그인 중 오류가 발생했습니다.');
        }
    });
}

// 회원가입 처리
const signupForm = document.querySelector('#signupModal .btn-submit');
if (signupForm) {
    signupForm.addEventListener('click', async function() {
        const name = document.querySelector('#signupModal input[placeholder="이름을 입력하세요"]').value;
        const email = document.querySelector('#signupModal input[placeholder="이메일을 입력하세요"]').value;
        const password = document.querySelector('#signupModal input[placeholder="비밀번호를 입력하세요 (8자 이상)"]').value;
        const passwordConfirm = document.querySelector('#signupModal input[placeholder="비밀번호를 다시 입력하세요"]').value;
        const nickname = document.querySelector('#signupModal input[placeholder="독서 커뮤니티에서 사용할 닉네임"]').value;
        const birthDate = document.querySelector('#signupModal input[type="date"]').value;
        const agreeTerms = document.querySelector('#signupModal input[type="checkbox"]').checked;
        
        // 유효성 검사
        if (!name || !email || !password || !nickname) {
            alert('필수 항목을 모두 입력해주세요.');
            return;
        }
        
        if (password.length < 8) {
            alert('비밀번호는 8자 이상이어야 합니다.');
            return;
        }
        
        if (password !== passwordConfirm) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }
        
        if (!agreeTerms) {
            alert('이용약관에 동의해주세요.');
            return;
        }
        
        // 선택된 장르 수집
        const favoriteGenres = [];
        document.querySelectorAll('.genre-checkbox input[type="checkbox"]:checked').forEach(checkbox => {
            favoriteGenres.push(checkbox.parentElement.textContent.trim());
        });
        
        const agreeMarketing = document.querySelectorAll('#signupModal input[type="checkbox"]')[1]?.checked || false;
        
        try {
            const response = await fetch('http://localhost:8080/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name,
                    email,
                    password,
                    nickname,
                    birthDate: birthDate || null,
                    favoriteGenres,
                    agreeTerms,
                    agreeMarketing
                })
            });
            
            if (response.ok) {
                const user = await response.json();
                alert('회원가입이 완료되었습니다!');
                signupModal.classList.remove('active');
                loginModal.classList.add('active');
            } else {
                const error = await response.text();
                alert(error || '회원가입에 실패했습니다.');
            }
        } catch (error) {
            console.error('Signup error:', error);
            alert('회원가입 중 오류가 발생했습니다.');
        }
    });
}
