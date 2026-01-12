package balancebite.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_weight_history")
public class WeightEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public WeightEntry() {}

    public WeightEntry(Double weight, LocalDate date, User user) {
        this.weight = weight;
        this.date = date;
        this.user = user;
    }

    // Getters en Setters
    public Long getId() { return id; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}