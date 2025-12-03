package com.example.databaseprogramming.ranking.service;

import com.example.databaseprogramming.login.entity.User;
import com.example.databaseprogramming.login.repository.UserRepository;
import com.example.databaseprogramming.quiz.repository.QuizAttemptRepository;
import com.example.databaseprogramming.quiz.repository.QuizLikeRepository; // 추가됨
import com.example.databaseprogramming.ranking.dto.RankingDto;
import com.example.databaseprogramming.reading.repository.ReadingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizLikeRepository quizLikeRepository; // 추가됨

    @Transactional(readOnly = true)
    public List<RankingDto> getRanking(String type, Long currentUserId) {
        List<User> allUsers = userRepository.findAll();
        List<RankingDto> rankingList = new ArrayList<>();

        for (User user : allUsers) {
            int score = 0;
            String label = "";
            int level = 1;

            int bookCount = readingRecordRepository.countByUserIdAndStatus(user.getId(), "완독");
            level = (bookCount / 3) + 1;

            if ("reading".equals(type)) {
                score = bookCount;
                label = score + "권";
            }
            else if ("quiz".equals(type)) {
                // 1. 퀴즈 정답 점수 (문제당 10점)
                int correctAnswers = quizAttemptRepository.countByUserIdAndIsCorrectTrue(user.getId());
                int quizScore = correctAnswers * 10;

                // 2. [추가] 내가 만든 퀴즈가 받은 좋아요 가산점 (1개당 5점)
                int receivedLikes = quizLikeRepository.countTotalLikesReceivedByCreator(user.getId());
                int likeBonus = receivedLikes * 5;

                score = quizScore + likeBonus;
                label = score + "점"; // (좋아요 보너스 포함)
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

        rankingList.sort(Comparator.comparingInt(RankingDto::getScore).reversed());

        for (int i = 0; i < rankingList.size(); i++) {
            rankingList.get(i).setRank(i + 1);
        }

        return rankingList;
    }
}