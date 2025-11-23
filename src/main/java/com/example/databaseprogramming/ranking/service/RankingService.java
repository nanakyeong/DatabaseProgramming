package com.example.databaseprogramming.ranking.service;

import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.repository.UserRepository;
import com.example.databaseprogramming.quiz.repository.QuizAttemptRepository;
import com.example.databaseprogramming.ranking.dto.RankingDto;
import com.example.databaseprogramming.reading.repository.ReadingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    // type: "reading" (독서량) 또는 "quiz" (퀴즈점수)
    @Transactional(readOnly = true)
    public List<RankingDto> getRanking(String type, Long currentUserId) {
        List<User> allUsers = userRepository.findAll();
        List<RankingDto> rankingList = new ArrayList<>();

        for (User user : allUsers) {
            int score = 0;
            String label = "";
            int level = 1; // 기본 레벨

            // 1. 독서량 데이터 계산
            int bookCount = readingRecordRepository.countByUserIdAndStatus(user.getId(), "완독");
            level = (bookCount / 3) + 1; // 3권마다 1레벨 상승 로직 (예시)

            if ("reading".equals(type)) {
                score = bookCount;
                label = score + "권";
            }
            // 2. 퀴즈 데이터 계산
            else if ("quiz".equals(type)) {
                int correctAnswers = quizAttemptRepository.countByUserIdAndIsCorrectTrue(user.getId());
                score = correctAnswers * 10; // 문제당 10점
                label = score + "점";
            }

            rankingList.add(RankingDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .level(level)
                    .score(score)
                    .scoreLabel(label)
                    .isMe(user.getId().equals(currentUserId))
                    .build());
        }

        // 3. 점수 내림차순 정렬
        rankingList.sort(Comparator.comparingInt(RankingDto::getScore).reversed());

        // 4. 등수 매기기
        for (int i = 0; i < rankingList.size(); i++) {
            rankingList.get(i).setRank(i + 1);
        }

        return rankingList;
    }
}