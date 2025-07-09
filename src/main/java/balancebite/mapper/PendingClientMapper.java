package balancebite.mapper;

import balancebite.dto.user.PendingClientDTO;
import balancebite.model.user.PendingClient;
import org.springframework.stereotype.Component;

@Component
public class PendingClientMapper {

    public PendingClientDTO toDTO(PendingClient pendingClient) {
        if (pendingClient == null) return null;

        return new PendingClientDTO(
                pendingClient.getId(),
                pendingClient.getEmail(),
                pendingClient.getDietitian().getId(),
                pendingClient.getDietitian().getUserName()
        );
    }
}
