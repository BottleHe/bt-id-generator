package work.bottle.plugin.id.consumer.client;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.id.consumer.client.config.IdPublisherProperties;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class IdServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(IdServiceTest.class);

    @Test
    public void testMultiThreadRequest() throws InterruptedException, ExecutionException {
        IdPublisherProperties idPublisherProperties = new IdPublisherProperties();
        List<String> domainList = new ArrayList<>();
        // domainList.add("http://127.0.0.1:17782");
        domainList.add("http://177.19.120.11:31782");
        domainList.add("http://177.19.120.12:31782");
        domainList.add("http://177.19.120.13:31782");
        idPublisherProperties.setServiceDomainList(domainList);
        final int TIMES = 1_000_000;
        final int THREAD_NUM = 100;
        FutureTask<Long[]>[] tasks = new FutureTask[THREAD_NUM];
        Thread[] threads = new Thread[THREAD_NUM];
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);
        Set<Long> idSet = new HashSet<>();
        for (int i = 0; i < THREAD_NUM; i++) {
            tasks[i] = new FutureTask<Long[]>(new Callable<Long[]>() {
                @Override
                public Long[] call() throws Exception {
                    IdService idServer = IdServiceFactory.getIdServer(idPublisherProperties);
                    Long[] rets = new Long[TIMES / THREAD_NUM];
                    for (int j = 0; j < TIMES / THREAD_NUM; j++) {
                        rets[j] = idServer.next();
                    }
                    countDownLatch.countDown();
                    return rets;
                }
            });
            threads[i] = new Thread(tasks[i], String.format("TH-%d-%d", THREAD_NUM, i));
        }

        logger.info("开始执行: {}", System.nanoTime());
        long start = System.nanoTime();
        for (int i = 0; i < THREAD_NUM; i++) {
            threads[i].start();
        }
        countDownLatch.await();
        long end = System.nanoTime();
        logger.info("结束执行: {}", end);
        System.out.println("共计" + TIMES + "次计算, 用时: " + (end - start) + "ns, " + ((end - start) / 1e6) + "ms, " +
                "每毫秒计算次数: " + (TIMES / ((end - start) / 1e6)) + ", " +
                "每秒计算次数: " + (TIMES / ((end - start) / 1e9)));
        // 获取结果, 进行SET计算
        for (int i = 0; i < THREAD_NUM; i++) {
            idSet.addAll(Arrays.asList(tasks[i].get()));
        }
        System.out.println("set size: " + idSet.size());

    }

    @Test
    public void testMultiThreadRequest2() throws InterruptedException, ExecutionException {
        IdPublisherProperties idPublisherProperties = new IdPublisherProperties();
        List<String> domainList = new ArrayList<>();
//        domainList.add("http://127.0.0.1:17782");

        domainList.add("http://177.19.120.11:31782");
        domainList.add("http://177.19.120.12:31782");
        domainList.add("http://177.19.120.13:31782");
        idPublisherProperties.setServiceDomainList(domainList);
        final int TIMES = 1_000_000;
        final int THREAD_NUM = 100;
        final int BATCH_NUM = 100;
        FutureTask<List<Long>>[] tasks = new FutureTask[THREAD_NUM];
        Thread[] threads = new Thread[THREAD_NUM];
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);
        Set<Long> idSet = new HashSet<>();
        for (int i = 0; i < THREAD_NUM; i++) {
            tasks[i] = new FutureTask<List<Long>>(new Callable<List<Long>>() {
                @Override
                public List<Long> call() throws Exception {
                    IdService idServer = IdServiceFactory.getIdServer(idPublisherProperties);
                    List<Long> rets = new ArrayList<Long>(TIMES / THREAD_NUM);
                    for (int j = 0; j < TIMES / THREAD_NUM / BATCH_NUM; j++) {
                        rets.addAll(idServer.next(BATCH_NUM));
                    }
                    countDownLatch.countDown();
                    return rets;
                }
            });
            threads[i] = new Thread(tasks[i], String.format("TH-%d-%d", THREAD_NUM, i));
        }

        logger.info("开始执行: {}", System.nanoTime());
        long start = System.nanoTime();
        for (int i = 0; i < THREAD_NUM; i++) {
            threads[i].start();
        }
        countDownLatch.await();
        long end = System.nanoTime();
        logger.info("结束执行: {}", end);
        System.out.println("共计" + TIMES + "次计算, 用时: " + (end - start) + "ns, " + ((end - start) / 1e6) + "ms, " +
                "每毫秒计算次数: " + (TIMES / ((end - start) / 1e6)) + ", " +
                "每秒计算次数: " + (TIMES / ((end - start) / 1e9)));
        // 获取结果, 进行SET计算
        for (int i = 0; i < THREAD_NUM; i++) {
            idSet.addAll(tasks[i].get());
        }
        System.out.println("set size: " + idSet.size());
    }
}
