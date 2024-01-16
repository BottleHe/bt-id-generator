package work.bottle.plugin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class DefaultConfig {
//    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(8, 128, 60000L, java.util.concurrent.TimeUnit.MILLISECONDS, new java.util.concurrent.ArrayBlockingQueue<>(1024), new java.util.concurrent.ThreadFactory() {
            private final java.util.concurrent.atomic.AtomicInteger atoi = new java.util.concurrent.atomic.AtomicInteger(0);

            @Override
            public Thread newThread(@org.jetbrains.annotations.NotNull Runnable r) {
                return new Thread(r, String.format("ID-PROVIDER-%d-%d", 4, atoi.getAndIncrement()));
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
