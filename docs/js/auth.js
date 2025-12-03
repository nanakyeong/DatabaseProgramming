document.addEventListener('DOMContentLoaded', function() {
    // === 기존 모달 관련 요소 ===
    const loginModal = document.getElementById('loginModal');
    const signupModal = document.getElementById('signupModal');
    const mypageModal = document.getElementById('mypageModal'); // 추가됨

    const btnLogin = document.querySelector('.btn-login');
    const btnSignup = document.querySelector('.btn-signup');

    // === 1. 로그인 상태 확인 및 UI 업데이트 ===
    checkLoginStatus();

    function checkLoginStatus() {
        const userJson = localStorage.getItem('user');
        const navButtons = document.querySelector('.nav-buttons');

        if (userJson && navButtons) {
            const user = JSON.parse(userJson);

            // [수정] 마이페이지 버튼 및 로그아웃 버튼 표시
            navButtons.innerHTML = `
                <span class="user-welcome" style="margin-right: 10px; font-size: 0.95rem;">
                    <strong>${user.nickname}</strong>님
                </span>
                <button class="btn-mypage" style="padding: 0.5rem 1rem; border: 1px solid #667eea; color: #667eea; background: white; cursor: pointer; border-radius: 4px; font-weight: 500;">마이페이지</button>
                <button class="btn-logout" style="margin-left: 5px; padding: 0.5rem 1rem; border: 1px solid #ddd; background: #f5f5f5; cursor: pointer; border-radius: 4px;">로그아웃</button>
            `;

            // 동적으로 생성된 버튼에 이벤트 리스너 추가
            document.querySelector('.btn-logout').addEventListener('click', logout);

            // 마이페이지 버튼 클릭 시
            document.querySelector('.btn-mypage').addEventListener('click', () => {
                // 모달 열 때 현재 정보 채워넣기
                const currentUser = JSON.parse(localStorage.getItem('user')); // 최신 정보 다시 로드
                if(document.getElementById('editNickname')) document.getElementById('editNickname').value = currentUser.nickname;
                if(document.getElementById('editProfileUrl')) document.getElementById('editProfileUrl').value = currentUser.profileImageUrl || '';

                // 비밀번호 필드 초기화
                document.getElementById('editNewPassword').value = '';
                document.getElementById('editCurrentPassword').value = '';

                // 탭 초기화 (정보 수정 탭으로)
                switchTab('profile');
                openModal(mypageModal);
            });

        } else {
            // 비로그인 상태: 기존 버튼 이벤트 연결
            if (btnLogin) btnLogin.addEventListener('click', () => openModal(loginModal));
            if (btnSignup) btnSignup.addEventListener('click', () => openModal(signupModal));
        }
    }

    // === 2. 로그아웃 함수 ===
    function logout() {
        if(confirm('로그아웃 하시겠습니까?')) {
            localStorage.removeItem('user'); // 저장된 유저 정보 삭제
            alert('로그아웃 되었습니다.');
            location.href = 'index.html'; // 메인으로 이동
        }
    }

    // === 3. 마이페이지 탭 전환 로직 ===
    const tabLinks = document.querySelectorAll('.mypage-tabs .tab-link');
    tabLinks.forEach(link => {
        link.addEventListener('click', function() {
            const tabName = this.dataset.tab;
            switchTab(tabName);
        });
    });

    function switchTab(tabName) {
        // 탭 버튼 스타일 업데이트
        document.querySelectorAll('.mypage-tabs .tab-link').forEach(t => {
            if(t.dataset.tab === tabName) t.classList.add('active');
            else t.classList.remove('active');
        });

        // 탭 컨텐츠 표시/숨김
        document.getElementById('tab-profile').style.display = tabName === 'profile' ? 'block' : 'none';
        document.getElementById('tab-withdraw').style.display = tabName === 'withdraw' ? 'block' : 'none';
    }

    // === 4. 회원 정보 수정 처리 ===
    const btnUpdateProfile = document.getElementById('btnUpdateProfile');
    if (btnUpdateProfile) {
        btnUpdateProfile.addEventListener('click', async () => {
            const user = JSON.parse(localStorage.getItem('user'));
            const nickname = document.getElementById('editNickname').value;
            const profileImageUrl = document.getElementById('editProfileUrl').value;
            const newPassword = document.getElementById('editNewPassword').value;
            const currentPassword = document.getElementById('editCurrentPassword').value;

            if (!currentPassword) {
                alert('본인 확인을 위해 현재 비밀번호를 입력해주세요.');
                return;
            }

            try {
                const res = await fetch('http://localhost:8080/api/users/profile', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        userId: user.id,
                        nickname,
                        profileImageUrl,
                        newPassword,
                        currentPassword
                    })
                });
                const data = await res.json();

                if (data.success) {
                    alert(data.message);
                    // 정보가 수정되었으므로 로그아웃 처리 (보안상 재로그인 권장)
                    localStorage.removeItem('user');
                    location.href = 'index.html';
                } else {
                    alert(data.message);
                }
            } catch (e) {
                console.error(e);
                alert('수정 중 오류가 발생했습니다.');
            }
        });
    }

    // === 5. 회원 탈퇴 처리 ===
    const btnWithdraw = document.getElementById('btnWithdraw');
    if (btnWithdraw) {
        btnWithdraw.addEventListener('click', async () => {
            if(!confirm('정말로 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return;

            const user = JSON.parse(localStorage.getItem('user'));
            const password = document.getElementById('withdrawPassword').value;

            if (!password) {
                alert('탈퇴 확인을 위해 비밀번호를 입력해주세요.');
                return;
            }

            try {
                const res = await fetch(`http://localhost:8080/api/users/withdraw?userId=${user.id}&password=${encodeURIComponent(password)}`, {
                    method: 'DELETE'
                });
                const data = await res.json();

                if (data.success) {
                    alert(data.message);
                    localStorage.removeItem('user');
                    location.href = 'index.html';
                } else {
                    alert(data.message);
                }
            } catch (e) {
                console.error(e);
                alert('오류가 발생했습니다.');
            }
        });
    }

    // === 6. 모달 공통 닫기 로직 ===
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', function() {
            closeModal(loginModal);
            closeModal(signupModal);
            closeModal(mypageModal);
        });
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(e) {
        if (e.target === loginModal) closeModal(loginModal);
        if (e.target === signupModal) closeModal(signupModal);
        if (e.target === mypageModal) closeModal(mypageModal);
    });

    // 모달 간 전환 (로그인 <-> 회원가입)
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

    // === 7. 로그인 요청 ===
    const loginFormBtn = loginModal?.querySelector('.btn-submit');
    if (loginFormBtn) {
        loginFormBtn.addEventListener('click', async function() {
            const email = loginModal.querySelector('input[type="email"]').value;
            const password = loginModal.querySelector('input[type="password"]').value;

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();

                if (data.success) {
                    alert('로그인 성공!');
                    localStorage.setItem('user', JSON.stringify(data.user));
                    closeModal(loginModal);
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

    // === 8. 회원가입 요청 ===
    const signupFormBtn = signupModal?.querySelector('.btn-submit');
    if (signupFormBtn) {
        signupFormBtn.addEventListener('click', async function() {
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
                const response = await fetch('http://localhost:8080/api/auth/signup', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
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