package work.bottle.plugin;

import com.sun.tools.javac.util.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class SimpleIdServiceTest {

    @Test
    public void highSwallowSimpleTest() {
        for (int j = 0; j < 10; j++) {
            SimpleIdService simpleIdService = new SimpleIdService(j);
            for (int i = 0; i < 10; i++) {
                long next = simpleIdService.next();
                System.out.println(next);
                DefaultIdConverter defaultIdConverter =  DefaultIdConverter.CURRENT;
                System.out.println(defaultIdConverter.convert(next));
            }
        }
    }

    @Test
    public void highPrecisionSimpleTest() {
        HighPrecisionIdGenerator highPrecisionIdGenerator = new HighPrecisionIdGenerator();
        for (int j = 0; j < 10; j++) {
            SimpleIdService simpleIdService = new SimpleIdService(j, highPrecisionIdGenerator);
            for (int i = 0; i < 10; i++) {
                long next = simpleIdService.next();
                System.out.println(next);
                DefaultIdConverter defaultIdConverter = DefaultIdConverter.CURRENT;
                System.out.println(defaultIdConverter.convert(next));
            }
        }
    }

    @Test
    public void highSwallowTest() {
        SimpleIdService simpleIdService = new SimpleIdService(0);
        int times = 1_000_000_000;
        int threadNum = 100;
        Assert.check(times % threadNum == 0);
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

    @Test
    public void highPrecisionTest() {
        SimpleIdService simpleIdService = new SimpleIdService(0, new HighPrecisionIdGenerator());
        int times = 1_000_000_000;
        int threadNum = 100;
        Assert.check(times % threadNum == 0);
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
