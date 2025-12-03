package com.example.databaseprogramming.global.init;

import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.repository.UserRepository;
import com.example.databaseprogramming.quiz.entity.Quiz;
import com.example.databaseprogramming.quiz.entity.QuizAttempt;
import com.example.databaseprogramming.quiz.repository.QuizAttemptRepository;
import com.example.databaseprogramming.quiz.repository.QuizRepository;
import com.example.databaseprogramming.reading.entity.ReadingRecord;
import com.example.databaseprogramming.reading.repository.ReadingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class Data implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 데이터가 없을 때만 초기화 수행
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("========== 더미 데이터 생성을 시작합니다 ==========");

        // 1. 사용자 생성 (주인공 + 랭킹용 들러리들)
        List<User> users = new ArrayList<>();

        // 주인공 계정 (Index 0)
        User mainUser = createUser("나경", "test@example.com", "나경", "1234");
        users.add(mainUser);

        // 랭킹용 더미 유저들
        users.add(createUser("김철수", "user1@example.com", "독서왕김철수", "1234"));
        users.add(createUser("이영희", "user2@example.com", "책벌레이영희", "1234"));
        users.add(createUser("박지은", "user3@example.com", "문학소녀박지은", "1234"));
        users.add(createUser("최민호", "user4@example.com", "개발자최민호", "1234"));
        users.add(createUser("정수진", "user5@example.com", "해리포터덕후", "1234"));

        userRepository.saveAll(users);

        // 2. 독서 기록 생성 (캘린더 & 랭킹용)
        // 주인공: 이번 달에 책을 열심히 읽은 척 (12권 정도, 연속 독서일 포함)
        LocalDate today = LocalDate.now();

        // (1) 오늘부터 5일 전까지 연속 기록 (연속 독서일 5일 만들기)
        createRecord(mainUser, "이방인", "알베르 카뮈", "완독", today);
        createRecord(mainUser, "데미안", "헤르만 헤세", "완독", today.minusDays(1));
        createRecord(mainUser, "1984", "조지 오웰", "완독", today.minusDays(2));
        createRecord(mainUser, "동물농장", "조지 오웰", "완독", today.minusDays(3));
        createRecord(mainUser, "호밀밭의 파수꾼", "J.D. 샐린저", "완독", today.minusDays(4));

        // (2) 이번 달 초반 기록 (캘린더 채우기)
        createRecord(mainUser, "코스모스", "칼 세이건", "읽는 중", today.minusDays(10));
        createRecord(mainUser, "사피엔스", "유발 하라리", "완독", today.minusDays(12));
        createRecord(mainUser, "총 균 쇠", "재레드 다이아몬드", "읽기 시작", today.minusDays(15));

        // (3) 다른 유저들 기록 (랭킹 경쟁용 - 김철수가 1등 하도록)
        // 김철수: 50권 (압도적 1등)
        generateRandomRecords(users.get(1), 50);
        // 이영희: 40권
        generateRandomRecords(users.get(2), 40);
        // 박지은: 30권
        generateRandomRecords(users.get(3), 30);
        // 최민호: 5권 (주인공보다 낮음)
        generateRandomRecords(users.get(4), 5);


        // 3. 퀴즈 생성
        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(createQuiz(users.get(1), "어린왕자", "생텍쥐페리", "어린왕자가 사는 별의 이름은?", "B-612", "B-613", "C-321", "Earth", "A", "쉬움"));
        quizzes.add(createQuiz(users.get(2), "1984", "조지 오웰", "주인공 윈스턴이 일하는 부서는?", "진리부", "평화부", "애정부", "풍요부", "A", "보통"));
        quizzes.add(createQuiz(users.get(3), "해리포터", "J.K. 롤링", "해리포터의 기숙사는?", "슬리데린", "그리핀도르", "후플푸프", "래번클로", "B", "쉬움"));
        quizzes.add(createQuiz(users.get(4), "채식주의자", "한강", "주인공 영혜가 거부하기 시작한 음식은?", "고기", "밀가루", "생선", "우유", "A", "어려움"));
        // 주인공이 만든 퀴즈 (내가 만든 퀴즈 개수 확인용)
        quizzes.add(createQuiz(mainUser, "클린 코드", "로버트 C. 마틴", "나쁜 코드를 짜지 않기 위해 필요한 것은?", "의지", "시간", "리팩토링", "팀장님의 사랑", "C", "보통"));

        quizRepository.saveAll(quizzes);

        // 4. 퀴즈 풀이 기록 (포인트 랭킹용)
        // 주인공: 몇 문제 풀어서 점수 획득
        createAttempt(mainUser, quizzes.get(0), true); // 정답
        createAttempt(mainUser, quizzes.get(1), true); // 정답
        createAttempt(mainUser, quizzes.get(2), false); // 오답
        createAttempt(mainUser, quizzes.get(3), true); // 정답

        // 랭킹 1등 유저(김철수)는 퀴즈도 많이 푼 척
        for(Quiz q : quizzes) {
            createAttempt(users.get(1), q, true);
        }

        System.out.println("========== 더미 데이터 생성 완료! ==========");
    }

    // --- 헬퍼 메소드들 ---

    private User createUser(String name, String email, String nickname, String password) {
        return User.builder()
                .name(name)
                .email(email)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build();
    }

    private void createRecord(User user, String title, String author, String status, LocalDate date) {
        readingRecordRepository.save(ReadingRecord.builder()
                .user(user)
                .title(title)
                .author(author)
                .pages(300)
                .status(status)
                .rating(5)
                .memo("정말 재미있는 책이었다.")
                .readingDate(date)
                .build());
    }

    private void generateRandomRecords(User user, int count) {
        LocalDate start = LocalDate.now().minusMonths(3);
        for (int i = 0; i < count; i++) {
            createRecord(user, "랜덤책 " + i, "작가 " + i, "완독", start.plusDays(i));
        }
    }

    private Quiz createQuiz(User creator, String bookTitle, String author, String question,
                            String opA, String opB, String opC, String opD, String answer, String diff) {
        return Quiz.builder()
                .creator(creator)
                .bookTitle(bookTitle)
                .bookAuthor(author)
                .question(question)
                .optionA(opA).optionB(opB).optionC(opC).optionD(opD)
                .correctAnswer(answer)
                .difficulty(diff)
                .explanation("이것은 정답에 대한 해설입니다.")
                .build();
    }

    private void createAttempt(User user, Quiz quiz, boolean correct) {
        quizAttemptRepository.save(QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .isCorrect(correct)
                .build());
    }
}