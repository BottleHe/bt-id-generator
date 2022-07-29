package work.bottle.demo.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import work.bottle.plugin.HighPrecisionIdGenerator;
import work.bottle.plugin.SimpleIdService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Component
public class WriteIdToRedisRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(WriteIdToRedisRunner.class);
    private static final String REDIS_KEY = "BT:TEST:KEY";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Application begin to run ... ");
        redisTemplate.delete(REDIS_KEY);
        SimpleIdService simpleIdService = new SimpleIdService(0);
        int times = 1_000_000_000;
        int threadNum = 100;
        Thread[] threads = new Thread[threadNum];
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < times / threadNum; j++) {
                        long id = simpleIdService.next();
                        // redisTemplate.opsForSet().add(REDIS_KEY, String.valueOf(id));
                    }
                    countDownLatch.countDown();
                }
            }, String.format("TEST-THREAD-%d-%d", threadNum, i));
        }
        long start = System.nanoTime();
        for (int i = 0; i < threadNum; i++) {
            threads[i].start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println("共计" + times + "次计算, 用时: " + (end - start) + "ns, " + ((end - start) / 1e6) + "ms, " +
                "每毫秒计算次数: " + (times / ((end - start) / 1e6)) + ", " +
                "每秒计算次数: " + (times / ((end - start) / 1e9)));

        logger.debug("共有数据条数: {}", redisTemplate.opsForSet().size(REDIS_KEY));

    }
}
