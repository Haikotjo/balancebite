package balancebite.model.user;

import jakarta.persistence.*;

@Entity
@Table(name = "pending_clients", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "dietitian_id"}))
public class PendingClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dietitian_id")
    private User dietitian;

    public PendingClient() {
    }

    public PendingClient(String email, User dietitian) {
        this.email = email;
        this.dietitian = dietitian;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getDietitian() {
        return dietitian;
    }

    public void setDietitian(User dietitian) {
        this.dietitian = dietitian;
    }
}
