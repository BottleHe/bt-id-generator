package work.bottle.plugin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdServiceSpringAppStartup {
    private static final Logger logger = LoggerFactory.getLogger(IdServiceSpringAppStartup.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(IdServiceSpringAppStartup.class, args);
        } catch (Exception e) {
            logger.error("IdServiceSpringAppStartup start failed", e);
        }
    }
}
