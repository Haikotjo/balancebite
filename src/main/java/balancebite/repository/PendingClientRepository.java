package balancebite.repository;

import balancebite.model.user.PendingClient;
import balancebite.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PendingClientRepository extends JpaRepository<PendingClient, Long> {

    Optional<PendingClient> findByEmailAndDietitian(String email, User dietitian);

    List<PendingClient> findAllByEmail(String email);

    List<PendingClient> findAllByDietitian(User dietitian);

    void deleteAllByEmail(String email);

}
