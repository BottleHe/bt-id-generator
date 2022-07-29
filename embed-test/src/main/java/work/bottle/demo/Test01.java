package work.bottle.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.HighPrecisionIdPopulator;
import work.bottle.plugin.HighSwallowIdService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Test01 {

    private static final Logger logger = LoggerFactory.getLogger(Test01.class);

    public static void main(String[] args) {
        test03();
    }

    public static void test01() {
        // Set<Long> testSet = new HashSet<>();

        HighSwallowIdService simpleIdService = new HighSwallowIdService(0);
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
        // logger.info("Set中ID个数为: {}", testSet.size());
    }

    public static void test02() {
        HighSwallowIdService simpleIdService = new HighSwallowIdService(0);
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
                "每毫秒计算次数: " + (times / ((end - start) / 1e6)) +
                "每秒计算次数: " + (times / ((end - start) / 1e9)));
    }

    public static void test03() {
        HighSwallowIdService simpleIdService = new HighSwallowIdService(0);
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
                "每毫秒计算次数: " + (times / ((end - start) / 1e6)) +
                "每秒计算次数: " + (times / ((end - start) / 1e9)));
    }
}
