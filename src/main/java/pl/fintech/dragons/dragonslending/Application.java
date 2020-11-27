package pl.fintech.dragons.dragonslending;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.fintech.dragons.dragonslending.common.ExcludeFromComponentScan;

@SpringBootApplication
@ComponentScan( excludeFilters = {
        @ComponentScan.Filter(ExcludeFromComponentScan.class)
})
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
