package balancebite.model;

import balancebite.model.DailyIntake;
import balancebite.repository.DailyIntakeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DailyIntakeRepository dailyIntakeRepository;

    public DataInitializer(DailyIntakeRepository dailyIntakeRepository) {
        this.dailyIntakeRepository = dailyIntakeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Controleer of de database al een DailyIntake bevat
        if (dailyIntakeRepository.count() == 0) {
            // Maak een nieuwe DailyIntake aan met de standaardwaarden
            DailyIntake dailyIntake = new DailyIntake();
            dailyIntakeRepository.save(dailyIntake);
            System.out.println("Standaard DailyIntake opgeslagen in de database.");
        } else {
            System.out.println("DailyIntake bestaat al in de database.");
        }
    }
}
