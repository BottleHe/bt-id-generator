package work.bottle.plugin.client;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultExecutorFactory implements ExecutorFactory {

    private static final Integer CORE_POOL_SIZE = 2;

    @Override
    public ExecutorService getExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    private final AtomicInteger atoi = new AtomicInteger(0);
                    @Override
                    public Thread newThread(@NotNull Runnable r) {
                        return new Thread(r, String.format("ID-PROVIDER-%d-%d", CORE_POOL_SIZE, atoi.getAndIncrement()));
                    }
                });
    }
}
