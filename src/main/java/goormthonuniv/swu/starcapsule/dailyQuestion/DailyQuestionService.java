package goormthonuniv.swu.starcapsule.dailyQuestion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyQuestionService {

    private final DailyQuestionRepository dailyQuestionRepository;

    // 오늘 날짜에 해당하는 질문을 가져옵니다.
    public Optional<DailyQuestion> getTodayQuestion() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 날짜만 비교
        return dailyQuestionRepository.findByDate(today);
    }
}
