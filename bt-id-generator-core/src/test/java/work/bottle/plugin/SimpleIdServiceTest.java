package work.bottle.plugin;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class SimpleIdServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleIdServiceTest.class);

    @Test
    public void testResolve() {
        long v = 1951374178145726l;
        // long v =  1951364703848831l;
        HighSwallowConvetor highSwallowConvetor = new HighSwallowConvetor();
        Id convert = highSwallowConvetor.convert(v);
        logger.info("时间为: {}", convert.getDateTime());
    }

    @Test
    public void highSwallowSimpleTest() {
        for (int j = 0; j < 10; j++) {
            IdService idService = new HighSwallowIdService(j);
            for (int i = 0; i < 10; i++) {
                long next = idService.next();
                System.out.println(next);
                IdConverter idConverter =  new HighSwallowConvetor();
                System.out.println(idConverter.convert(next));
            }
        }
    }

    @Test
    public void highPrecisionSimpleTest() {
        for (int j = 0; j < 10; j++) {
            IdService IdService = new HighPrecisionIdService(j);
            for (int i = 0; i < 10; i++) {
                long next = IdService.next();
                System.out.println(next);
                IdConverter idConverter = new HighPrecisionConvertor();
                System.out.println(idConverter.convert(next));
            }
        }
    }

    @Test
    public void testHighSwallowUniqueness() {
        //redisTemplate.delete(REDIS_KEY);
        IdService idService = new HighSwallowIdService(0);
        int times = 10_000_000;
        int threadNum = 100;
        Thread[] threads = new Thread[threadNum];
        FutureTask<Long[]>[] tasks = new FutureTask[threadNum];
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        Set<Long> dataSet = new HashSet<>();
        for (int i = 0; i < threads.length; i++) {
            tasks[i] = new FutureTask<>(new Callable<Long[]>() {
                @Override
                public Long[] call() throws Exception {
                    Long[] ret = new Long[times / threadNum];
                    for (int j = 0; j < times / threadNum; j++) {
                        long id = idService.next();
                        ret[j] = id;
                    }
                    countDownLatch.countDown();
                    return ret;
                }
            });
            threads[i] = new Thread(tasks[i], String.format("TEST-THREAD-%d-%d", threadNum, i));
        }
        logger.info("开始执行: {}", System.nanoTime());
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
        logger.info("结束执行: {}", end);
        System.out.println("共计" + times + "次计算, 用时: " + (end - start) + "ns, " + ((end - start) / 1e6) + "ms, " +
                "每毫秒计算次数: " + (times / ((end - start) / 1e6)) + ", " +
                "每秒计算次数: " + (times / ((end - start) / 1e9)));
        for (int i = 0; i < threadNum; i++) {
            Long[] longs = null;
            try {
                longs = tasks[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            dataSet.addAll(Arrays.asList(Optional.ofNullable(longs).orElse(new Long[0])));
        }

        // logger.debug("共有数据条数: {}", redisTemplate.opsForSet().size(REDIS_KEY));
        System.out.println("共有数据条数: " + dataSet.size());
        Assert.assertEquals("最终数据不唯一", times, dataSet.size());
    }

    @Test
    public void testHighPrecisionUniqueness() {
        IdService idService = new HighPrecisionIdService(0);
        int times = 50_000_000;
        int threadNum = 100;
        Thread[] threads = new Thread[threadNum];
        FutureTask<Long[]>[] tasks = new FutureTask[threadNum];
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        Set<Long> dataSet = new HashSet<>();
        for (int i = 0; i < threads.length; i++) {
            tasks[i] = new FutureTask<>(new Callable<Long[]>() {
                @Override
                public Long[] call() throws Exception {
                    Long[] ret = new Long[times / threadNum];
                    for (int j = 0; j < times / threadNum; j++) {
                        long id = idService.next();
                        ret[j] = id;
                    }
                    countDownLatch.countDown();
                    return ret;
                }
            });
            threads[i] = new Thread(tasks[i], String.format("TEST-THREAD-%d-%d", threadNum, i));
        }
        logger.info("开始执行: {}", System.nanoTime());
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
        logger.info("结束执行: {}", end);
        System.out.println("共计" + times + "次计算, 用时: " + (end - start) + "ns, " + ((end - start) / 1e6) + "ms, " +
                "每毫秒计算次数: " + (times / ((end - start) / 1e6)) + ", " +
                "每秒计算次数: " + (times / ((end - start) / 1e9)));
        for (int i = 0; i < threadNum; i++) {
            Long[] longs = null;
            try {
                longs = tasks[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            dataSet.addAll(Arrays.asList(Optional.ofNullable(longs).orElse(new Long[0])));
        }

        // logger.debug("共有数据条数: {}", redisTemplate.opsForSet().size(REDIS_KEY));
        System.out.println("共有数据条数: " + dataSet.size());
        Assert.assertEquals("最终数据不唯一", times, dataSet.size());
    }
}
