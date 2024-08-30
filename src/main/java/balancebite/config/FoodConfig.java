package balancebite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "food")
public class FoodConfig {
    private List<String> fdcIds;

    public List<String> getFdcIds() {
        return fdcIds;
    }

    public void setFdcIds(List<String> fdcIds) {
        this.fdcIds = fdcIds;
    }
}
