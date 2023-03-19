package work.bottle.plugin.id.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static work.bottle.plugin.id.publisher.utils.HostUtils.loadHostNameList;

@SpringBootApplication
public class IdPublisherServiceStartup {

    private static final Logger logger = LoggerFactory.getLogger(IdPublisherServiceStartup.class);

    public static void main(String[] args) {
        try {
//            logger.info("Loading local hostnames ... ");
//            loadHostNameList();
            SpringApplication.run(IdPublisherServiceStartup.class, args);
        } catch (Exception e) {
            logger.error("Startup error", e);
        }
    }
}
