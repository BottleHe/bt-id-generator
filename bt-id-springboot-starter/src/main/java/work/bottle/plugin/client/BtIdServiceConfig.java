package work.bottle.plugin.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import work.bottle.plugin.id.consumer.client.IdService;
import work.bottle.plugin.id.consumer.client.IdServiceFactory;

import java.util.concurrent.ExecutorService;

@Component
@AutoConfiguration(before = {BtIdProperties.class})
public class BtIdServiceConfig {

    private final BtIdProperties idPublisherProperties;
    private final IdService idService;
    private ExecutorService executorService;

    public BtIdServiceConfig(BtIdProperties btIdProperties, ExecutorFactory executorFactory) {
        this.idPublisherProperties = btIdProperties;
        this.idService = IdServiceFactory.getIdServer(idPublisherProperties);
        this.executorService = executorFactory.getExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(IdService.class)
    public IdService idService() {
        return new IdBucket(idService, executorService);
    }
}
