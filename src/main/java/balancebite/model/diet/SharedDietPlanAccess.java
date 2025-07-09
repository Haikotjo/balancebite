package balancebite.model.diet;

import balancebite.model.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "shared_diet_plan_access")
public class SharedDietPlanAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id", nullable = false)
    private DietPlan dietPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String email;

    public SharedDietPlanAccess() {
    }

    public SharedDietPlanAccess(DietPlan dietPlan, User user, String email) {
        this.dietPlan = dietPlan;
        this.user = user;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public DietPlan getDietPlan() {
        return dietPlan;
    }

    public void setDietPlan(DietPlan dietPlan) {
        this.dietPlan = dietPlan;
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
        return "SharedDietPlanAccess{" +
                "id=" + id +
                ", dietPlan=" + (dietPlan != null ? dietPlan.getId() : null) +
                ", user=" + (user != null ? user.getId() : null) +
                ", email='" + email + '\'' +
                '}';
    }
}
