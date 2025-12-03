document.addEventListener('DOMContentLoaded', function() {
    const rankingList = document.getElementById('rankingList');
    const myRankCard = document.getElementById('myRankCard');
    const rankingTitle = document.getElementById('rankingTitle');
    const tabs = document.querySelectorAll('.rank-tab');

    // 현재 선택된 랭킹 타입 (기본: 독서량)
    let currentType = 'reading';

    // 1. 초기 로드
    loadRanking(currentType);

    // 2. 탭 클릭 이벤트
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // 탭 스타일 활성화
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // 데이터 로드
            currentType = tab.dataset.type;
            rankingTitle.textContent = currentType === 'reading' ? '독서량 순위' : '퀴즈 포인트 순위';
            loadRanking(currentType);
        });
    });

    // 3. 랭킹 데이터 로드 및 렌더링 함수
    async function loadRanking(type) {
        const userJson = localStorage.getItem('user');
        if (!userJson) {
            rankingList.innerHTML = '<p style="text-align:center; padding:20px;">랭킹을 보려면 로그인이 필요합니다.</p>';
            return;
        }
        const user = JSON.parse(userJson);

        try {
            const response = await fetch(`http://localhost:8080/api/ranking?type=${type}&userId=${user.id}`);
            const data = await response.json();

            renderRankingList(data);
            renderMyRank(data, user.id);

        } catch (error) {
            console.error('Ranking Load Error:', error);
            rankingList.innerHTML = '<p style="text-align:center;">데이터를 불러오는 중 오류가 발생했습니다.</p>';
        }
    }

    // 리스트 렌더링
    function renderRankingList(data) {
        rankingList.innerHTML = ''; // 초기화

        if (data.length === 0) {
            rankingList.innerHTML = '<p style="text-align:center; padding:20px;">아직 기록된 순위가 없습니다.</p>';
            return;
        }

        data.forEach(item => {
            const isTop3 = item.rank <= 3;
            let rankBadgeHtml = '';

            // 1,2,3등 뱃지 처리
            if (item.rank === 1) rankBadgeHtml = `<div class="rank-badge gold"><span class="trophy">🏆</span></div>`;
            else if (item.rank === 2) rankBadgeHtml = `<div class="rank-badge silver"><span class="medal">🥈</span></div>`;
            else if (item.rank === 3) rankBadgeHtml = `<div class="rank-badge bronze"><span class="medal">🥉</span></div>`;
            else rankBadgeHtml = `<div class="rank-number">${item.rank}</div>`;

            // 본인 표시 클래스
            const myRankClass = item.isMe ? 'my-rank' : '';
            const myBadge = item.isMe ? '<span class="my-badge">나</span>' : '';

            // 프로필 이미지 (랜덤 아바타 API 사용, ID 기반으로 고정된 이미지 나오게 설정)
            const avatarUrl = `https://i.pravatar.cc/150?u=${item.userId}`;

            const html = `
                <div class="rank-item ${myRankClass} rank-${item.rank}">
                    ${rankBadgeHtml}
                    <div class="user-info">
                        <img src="${avatarUrl}" alt="프로필" class="user-avatar">
                        <div>
                            <h4>${item.nickname} ${myBadge}</h4>
                            <p class="user-level">레벨 ${item.level}</p>
                        </div>
                    </div>
                    <div class="rank-score">
                        <span class="score-value">${item.scoreLabel}</span>
                    </div>
                </div>
            `;
            rankingList.innerHTML += html;
        });
    }

    // 내 순위 카드 렌더링
    function renderMyRank(data, myUserId) {
        const myData = data.find(item => item.userId === myUserId);

        if (myData) {
            myRankCard.style.display = 'flex';
            myRankCard.innerHTML = `
                <div class="my-rank-badge">
                    <div class="rank-number">${myData.rank}위</div>
                    <p class="rank-label">현재 순위</p>
                </div>
                <div class="user-profile">
                    <div class="profile-avatar">
                        <img src="https://i.pravatar.cc/150?u=${myData.userId}" alt="프로필">
                    </div>
                    <div class="profile-info">
                        <h3>${myData.nickname} (나)</h3>
                        <p>레벨 ${myData.level}</p>
                    </div>
                </div>
                <div class="rank-stats">
                    <p>${myData.scoreLabel}</p>
                </div>
            `;
        } else {
            myRankCard.style.display = 'none';
        }
    }
});