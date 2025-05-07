package balancebite.repository;

import balancebite.model.diet.DietDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DietDayRepository extends JpaRepository<DietDay, Long> {
    List<DietDay> findByDate(LocalDate date);
    List<DietDay> findByDietId(Long dietId);
}
