package goormthonuniv.swu.starcapsule.myMemory;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MyMemoryRepository extends JpaRepository<MyMemory, Long> {

    @Query("SELECT COUNT(m) > 0 FROM MyMemory m WHERE m.createAt BETWEEN :startOfDay AND :endOfDay AND m.user.email = :email")
    boolean existsByCreateAtAndEmail(@Param("startOfDay") LocalDateTime startOfDay,
                                     @Param("endOfDay") LocalDateTime endOfDay,
                                     @Param("email") String email);

    @Query("SELECT m FROM MyMemory m WHERE m.createAt >= :startDate AND m.createAt < :endDate AND m.user.email = :email")
    List<MyMemory> findByCreateAtBetweenAndEmail(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM MyMemory m WHERE m.user.email = :userEmail AND FUNCTION('DATE', m.createAt) = :date")
    boolean existsByUserEmailAndDate(@Param("userEmail") String userEmail, @Param("date") LocalDate date);

}
