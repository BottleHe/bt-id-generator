package work.bottle.demo.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import work.bottle.plugin.SimpleIdService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Component
public class WriteIdToRedisRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(WriteIdToRedisRunner.class);
    private static final String REDIS_KEY = "BT:TEST:KEY";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        test3();
    }

    public void test3() {
        SimpleIdService simpleIdService = new SimpleIdService(0);
        int times = 1_000_000_000;
        int threadNum = 100;
        Thread[] threads = new Thread[threadNum];
        FutureTask<Integer>[] tasks = new FutureTask[threadNum];
        for (int i = 0; i < threads.length; i++) {
            tasks[i] = new FutureTask<Integer>(new Callable<Integer>() {
                @Override
                public Integer call() {
                    int _times = 0;
                    int n = times / threadNum;
                    int m = 0;
                    int t = n % 100 > 0 ? (n / 100) + 1 : n / 100;
                    for (int j = 0; j < t; j++) {
                        m = n - j * 100 >= 100 ? 100 : n - j * 100;
                        List<Long> ids = simpleIdService.next(m);
                        // testSet.addAll(ids);
                        _times += m;
                    }
                    return _times;
                }
            });
            threads[i] = new Thread(tasks[i], String.format("TEST-THREAD-%d-%d", threadNum, i));
        }
        long start = System.nanoTime();
        for (int i = 0; i < threadNum; i++) {
            threads[i].start();
        }
        int _times = 0;
        for (int i = 0; i < threadNum; i++) {
            try {
                _times += tasks[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        long end = System.nanoTime();
        System.out.println("共计" + times + "[" + _times + "]次计算, 用时: " + (end - start) + "ns, " + ((end - start) / 1e6) + "ms, " +
                "每毫秒计算次数: " + (times / ((end - start) / 1e6)) +
                "每秒计算次数: " + (times / ((end - start) / 1e9)));
    }

    public void test2() {
        logger.debug("Application begin to run ... ");
        redisTemplate.delete(REDIS_KEY);
        SimpleIdService simpleIdService = new SimpleIdService(0);
        int times = 100_000_000;
        int threadNum = 100;
        Thread[] threads = new Thread[threadNum];
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        FutureTask<List<Long>>[] tasks = new FutureTask[threadNum];
        for (int i = 0; i < threads.length; i++) {
            tasks[i] = new FutureTask<>(new Callable<List<Long>>() {
                @Override
                public List<Long> call() throws Exception {
                    List<Long> tmpList = new ArrayList<>();
                    for (int j = 0; j < times / threadNum; j++) {
                        long id = simpleIdService.next();
                        tmpList.add(id);
                    }
                    return tmpList;
                }
            });
            threads[i] = new Thread(tasks[i], String.format("TEST-THREAD-%d-%d", threadNum, i));
        }
        long start = System.nanoTime();
        for (int i = 0; i < threadNum; i++) {
            threads[i].start();
        }
        Set<Long> tmpSet = new HashSet<>();
        for (int i = 0; i < threadNum; i++) {
            List<Long> longs = null;
            try {
                longs = tasks[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            tmpSet.addAll(Optional.ofNullable(longs).orElse(new ArrayList<>()));
        }
        long end = System.nanoTime();
        System.out.println("共计" + times + "次计算, 用时: " + (end - start) + "ns, " + ((end - start) / 1e6) + "ms, " +
                "每毫秒计算次数: " + (times / ((end - start) / 1e6)) + ", " +
                "每秒计算次数: " + (times / ((end - start) / 1e9)));

        logger.debug("共有数据条数: {}", redisTemplate.opsForSet().size(REDIS_KEY));
        logger.debug("Set中共有数据 {}条", tmpSet.size());
    }

    public void test1() {
        logger.debug("Application begin to run ... ");
        redisTemplate.delete(REDIS_KEY);
        SimpleIdService simpleIdService = new SimpleIdService(0);
        int times = 10_000_000;
        int threadNum = 100;
        Thread[] threads = new Thread[threadNum];
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < times / threadNum; j++) {
                        long id = simpleIdService.next();
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
