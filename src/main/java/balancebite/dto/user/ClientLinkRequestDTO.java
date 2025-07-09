package balancebite.dto.user;

public class ClientLinkRequestDTO {

    private String clientEmail;

    public ClientLinkRequestDTO() {
    }

    public ClientLinkRequestDTO(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
}
