document.addEventListener('DOMContentLoaded', function() {
    // === 기존 모달 관련 요소 ===
    const loginModal = document.getElementById('loginModal');
    const signupModal = document.getElementById('signupModal');
    const btnLogin = document.querySelector('.btn-login');
    const btnSignup = document.querySelector('.btn-signup');

    // === 1. 로그인 상태 확인 및 UI 업데이트 (핵심 추가 기능) ===
    checkLoginStatus();

    function checkLoginStatus() {
        const userJson = localStorage.getItem('user');
        const navButtons = document.querySelector('.nav-buttons');

        if (userJson && navButtons) {
            const user = JSON.parse(userJson);

            // 로그인 상태라면: 버튼 영역을 닉네임과 로그아웃 버튼으로 교체
            navButtons.innerHTML = `
                <span class="user-welcome"><strong>${user.nickname}</strong>님 환영합니다!</span>
                <button class="btn-logout" style="margin-left: 10px; padding: 0.5rem 1rem; border: 1px solid #ddd; background: white; cursor: pointer; border-radius: 4px;">로그아웃</button>
            `;

            // 동적으로 생성된 로그아웃 버튼에 이벤트 리스너 추가
            const btnLogout = document.querySelector('.btn-logout');
            btnLogout.addEventListener('click', function() {
                logout();
            });

            // (선택사항) 로그인/회원가입 버튼 이벤트 리스너가 사라지므로 모달 관련 코드는 실행되지 않음
        } else {
            // 비로그인 상태라면: 기존 로그인/회원가입 버튼 유지 (HTML 그대로 둠)
            // 이벤트 리스너 재연결이 필요할 수 있으므로 기존 로직 유지
            if (btnLogin) btnLogin.addEventListener('click', () => openModal(loginModal));
            if (btnSignup) btnSignup.addEventListener('click', () => openModal(signupModal));
        }
    }

    // === 2. 로그아웃 함수 ===
    function logout() {
        if(confirm('로그아웃 하시겠습니까?')) {
            localStorage.removeItem('user'); // 저장된 유저 정보 삭제
            alert('로그아웃 되었습니다.');
            location.reload(); // 페이지 새로고침하여 UI 초기화
        }
    }

    // === 기존 모달 및 기능 로직 (유지) ===

    // 모달 닫기 버튼들
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', function() {
            closeModal(loginModal);
            closeModal(signupModal);
        });
    });

    // 모달 외부 클릭시 닫기
    window.addEventListener('click', function(e) {
        if (e.target === loginModal) closeModal(loginModal);
        if (e.target === signupModal) closeModal(signupModal);
    });

    // 모달 간 전환
    const showSignupFromLogin = document.getElementById('showSignupFromLogin');
    const showLoginFromSignup = document.getElementById('showLoginFromSignup');

    if (showSignupFromLogin) {
        showSignupFromLogin.addEventListener('click', (e) => {
            e.preventDefault();
            closeModal(loginModal);
            openModal(signupModal);
        });
    }

    if (showLoginFromSignup) {
        showLoginFromSignup.addEventListener('click', (e) => {
            e.preventDefault();
            closeModal(signupModal);
            openModal(loginModal);
        });
    }

    // === 로그인 처리 ===
    const loginForm = loginModal?.querySelector('.modal-footer .btn-submit');
    if (loginForm) {
        loginForm.addEventListener('click', async function() {
            const email = loginModal.querySelector('input[type="email"]').value;
            const password = loginModal.querySelector('input[type="password"]').value;

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', { // 포트 번호 확인 필요
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();

                if (data.success) {
                    alert('로그인 성공!');
                    // 로그인 성공 시 로컬 스토리지에 유저 정보 저장
                    localStorage.setItem('user', JSON.stringify(data.user));
                    closeModal(loginModal);

                    // 페이지 새로고침 (checkLoginStatus가 다시 실행되어 UI가 바뀜)
                    location.reload();
                } else {
                    alert(data.message);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('로그인 중 오류가 발생했습니다.');
            }
        });
    }

    // === 회원가입 처리 ===
    const signupForm = signupModal?.querySelector('.modal-footer .btn-submit');
    if (signupForm) {
        signupForm.addEventListener('click', async function() {
            const inputs = signupModal.querySelectorAll('.form-input');
            const name = inputs[0].value;
            const email = inputs[1].value;
            const password = inputs[2].value;
            const passwordConfirm = inputs[3].value;
            const nickname = inputs[4].value;

            if (password !== passwordConfirm) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/auth/signup', { // 포트 번호 확인 필요
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ name, email, password, nickname })
                });

                const data = await response.json();

                if (data.success) {
                    alert('회원가입 성공! 로그인해주세요.');
                    closeModal(signupModal);
                    openModal(loginModal);
                } else {
                    alert(data.message);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('회원가입 중 오류가 발생했습니다.');
            }
        });
    }
});

function openModal(modal) {
    if (modal) modal.classList.add('active');
}

function closeModal(modal) {
    if (modal) modal.classList.remove('active');
}