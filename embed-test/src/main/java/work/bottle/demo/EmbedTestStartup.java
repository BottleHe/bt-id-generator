package work.bottle.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmbedTestStartup {
    private static final Logger logger = LoggerFactory.getLogger(EmbedTestStartup.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(EmbedTestStartup.class, args);
        } catch (Exception e) {
            logger.error("Startup error", e);
        }
    }
}
