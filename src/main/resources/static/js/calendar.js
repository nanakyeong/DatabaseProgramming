// === DOM 요소 선택 ===
const currentMonthYear = document.getElementById('currentMonthYear');
const calendarGrid = document.getElementById('calendarGrid');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const modalDateValue = document.getElementById('modalDateValue');
const readingModal = document.getElementById('readingModal');

// === 상태 변수 (오늘 날짜 기준 초기화) ===
let currentDate = new Date();

// === 캘린더 렌더링 함수 ===
async function renderCalendar() {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth(); // 0(1월) ~ 11(12월)

    // 1. 헤더 텍스트 업데이트
    currentMonthYear.innerText = `${year}년 ${month + 1}월`;

    // 2. 백엔드에서 데이터 가져오기 (비동기)
    let calendarData = {};
    const userJson = localStorage.getItem('user');

    if (userJson) {
        const user = JSON.parse(userJson);
        try {
            // API 호출
            const response = await fetch(`http://localhost:8080/api/reading/calendar?userId=${user.id}&year=${year}&month=${month + 1}`);
            if (response.ok) {
                calendarData = await response.json();
            }
        } catch (e) {
            console.error("캘린더 데이터를 불러오는 중 오류 발생:", e);
        }
    }

    // 3. 그리드 초기화
    calendarGrid.innerHTML = `
        <div class="day-header">월</div>
        <div class="day-header">화</div>
        <div class="day-header">수</div>
        <div class="day-header">목</div>
        <div class="day-header">금</div>
        <div class="day-header">토</div>
        <div class="day-header">일</div>
    `;

    // 4. 날짜 계산
    const firstDayOfWeek = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();
    const emptySlots = (firstDayOfWeek + 6) % 7;

    for (let i = 0; i < emptySlots; i++) {
        const emptyDiv = document.createElement('div');
        emptyDiv.classList.add('day', 'empty');
        calendarGrid.appendChild(emptyDiv);
    }

    // 5. 날짜 채우기
    for (let day = 1; day <= lastDate; day++) {
        const dayDiv = document.createElement('div');
        dayDiv.classList.add('day');
        dayDiv.innerText = day;

        // 오늘 날짜 표시
        const today = new Date();
        if (year === today.getFullYear() && month === today.getMonth() && day === today.getDate()) {
            dayDiv.classList.add('today');
        }

        // 서버 데이터 적용
        const dateKey = `${year}-${month + 1}-${day}`;
        if (calendarData[dateKey]) {
            const className = calendarData[dateKey];
            dayDiv.classList.add(className);
        }

        // 날짜 클릭 이벤트 (모달 열기)
        dayDiv.addEventListener('click', () => {
            openReadingModal(year, month + 1, day);
        });

        calendarGrid.appendChild(dayDiv);
    }
}

// === 모달 열기 함수 ===
async function openReadingModal(year, month, day) {
    const modal = document.getElementById('readingModal');
    if (modal) {
        // 1. 날짜 텍스트 업데이트
        const dateValueSpan = document.getElementById('modalDateValue');
        if (dateValueSpan) {
            dateValueSpan.innerText = `${year}년 ${month}월 ${day}일`;
        }

        // 2. 모달 데이터 로드 및 버튼 설정 (modal.js에 정의된 함수 호출)
        if (window.loadModalData) {
            await window.loadModalData(year, month, day);
        }

        // 3. 모달 표시
        modal.classList.add('active');
    }
}

// === 버튼 이벤트 리스너 ===
prevBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});

nextBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});

// === [추가됨] Hero 섹션 '독서 기록 시작하기' 버튼 연결 ===
const btnStart = document.querySelector('.btn-start');
if (btnStart) {
    btnStart.addEventListener('click', () => {
        // 오늘 날짜 기준으로 모달 열기
        const today = new Date();
        openReadingModal(today.getFullYear(), today.getMonth() + 1, today.getDate());
    });
}

// === 대시보드 통계 로드 ===
async function loadDashboardStats() {
    const userJson = localStorage.getItem('user');
    if (!userJson) return;

    const user = JSON.parse(userJson);

    try {
        const response = await fetch(`http://localhost:8080/api/reading/stats?userId=${user.id}`);
        if (!response.ok) throw new Error('통계 불러오기 실패');

        const stats = await response.json();

        const statMonthly = document.getElementById('statMonthly');
        const statConsecutive = document.getElementById('statConsecutive');

        if (statMonthly) statMonthly.textContent = `${stats.monthlyCount}권`;
        if (statConsecutive) statConsecutive.textContent = `${stats.consecutiveDays}일`;

        const goalValue = document.getElementById('goalValue');
        const goalProgressBar = document.getElementById('goalProgressBar');
        const goalStatus = document.getElementById('goalStatus');

        if (goalValue) {
            goalValue.textContent = `${stats.weeklyGoalTarget}권`;
            const percent = Math.min(100, Math.round((stats.weeklyGoalAchieved / stats.weeklyGoalTarget) * 100));
            if (goalProgressBar) goalProgressBar.style.width = `${percent}%`;
            if (goalStatus) goalStatus.textContent = `${stats.weeklyGoalAchieved}/${stats.weeklyGoalTarget}권 (${percent}%)`;
        }

        const recentList = document.getElementById('recentList');
        if (recentList && stats.recentRecords.length > 0) {
            recentList.innerHTML = '';

            stats.recentRecords.forEach(record => {
                const li = document.createElement('li');
                li.className = 'reading-item';

                const dateObj = new Date(record.readingDate);
                const dateStr = `${dateObj.getMonth() + 1}월 ${dateObj.getDate()}일`;

                let badgeClass = 'reading';
                if (record.status === '완독') badgeClass = 'complete';
                else if (record.status === '읽기 시작') badgeClass = 'start';

                li.innerHTML = `
                    <div class="book-info">
                        <h4>${record.title}</h4>
                        <p>${dateStr} · ${record.pages || 0}페이지</p>
                    </div>
                    <span class="badge ${badgeClass}">${record.status}</span>
                `;
                recentList.appendChild(li);
            });
        }

    } catch (e) {
        console.error("대시보드 통계 로드 중 오류:", e);
    }
}

// 초기 실행
document.addEventListener('DOMContentLoaded', () => {
    renderCalendar();
    loadDashboardStats();
});