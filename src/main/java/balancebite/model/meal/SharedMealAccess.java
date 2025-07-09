package balancebite.model.meal;

import balancebite.model.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "shared_meal_access")
public class SharedMealAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String email;

    public SharedMealAccess() {
    }

    public SharedMealAccess(Meal meal, User user, String email) {
        this.meal = meal;
        this.user = user;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SharedMealAccess{" +
                "id=" + id +
                ", meal=" + (meal != null ? meal.getId() : null) +
                ", user=" + (user != null ? user.getId() : null) +
                ", email='" + email + '\'' +
                '}';
    }
}
