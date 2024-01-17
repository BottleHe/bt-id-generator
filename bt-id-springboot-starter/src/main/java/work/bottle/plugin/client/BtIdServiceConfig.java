package work.bottle.plugin.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import work.bottle.plugin.id.consumer.client.IdService;
import work.bottle.plugin.id.consumer.client.IdServiceFactory;

import java.util.concurrent.*;

@Configuration
@ComponentScan(basePackages = {"work.bottle.plugin.client"})
@AutoConfiguration(before = {BtIdProperties.class})
public class BtIdServiceConfig {

    private final IdService idService;

    public BtIdServiceConfig(BtIdProperties btIdProperties) {
        this.idService = IdServiceFactory.getIdServer(btIdProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorService.class)
    public ExecutorService executorService() {
//        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
//                60000L, TimeUnit.MILLISECONDS,
//                new ArrayBlockingQueue<>(MAX_QUEUE_CAPACITY),
//                new ThreadFactory() {
//                    private final AtomicInteger atoi = new AtomicInteger(0);
//
//                    @Override
//                    public Thread newThread(@NotNull Runnable r) {
//                        return new Thread(r, String.format("ID-PROVIDER-%d-%d", CORE_POOL_SIZE, atoi.getAndIncrement()));
//                    }
//                }, new ThreadPoolExecutor.CallerRunsPolicy());
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(IdService.class)
    public IdService idService(ExecutorService executorService) {
        return new IdBucket(idService, executorService);
    }
}
