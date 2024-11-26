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
        LocalDateTime todayStartOfDay = LocalDate.now(ZoneId.of("Asia/Seoul")).atStartOfDay();
        return dailyQuestionRepository.findByDate(todayStartOfDay);
    }
}
