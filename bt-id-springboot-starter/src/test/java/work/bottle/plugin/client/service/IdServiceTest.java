package work.bottle.plugin.client.service;

import jakarta.annotation.Resource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.client.IdBucket;
import work.bottle.plugin.id.consumer.client.IdService;
import work.bottle.plugin.id.consumer.client.IdServiceFactory;
import work.bottle.plugin.id.consumer.client.config.IdPublisherProperties;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class IdServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(IdServiceTest.class);

    @Resource
    private IdService idService;

    @Test
    public void testGetMultipleId() throws Exception {
        IdPublisherProperties idPublisherProperties = new IdPublisherProperties();
        List<String> domainList = new ArrayList<>();
        // domainList.add("http://127.0.0.1:17782");
        domainList.add("http://177.19.120.11:31782");
        domainList.add("http://177.19.120.12:31782");
        domainList.add("http://177.19.120.13:31782");
        idPublisherProperties.setServiceDomainList(domainList);

        this.idService = new IdBucket(IdServiceFactory.getIdServer(idPublisherProperties), null);

        final int TIMES = 1_000_000;
        final int THREAD_NUM = 100;
        FutureTask<Long[]>[] tasks = new FutureTask[THREAD_NUM];
        Thread[] threads = new Thread[THREAD_NUM];
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);
        Set<Long> idSet = new HashSet<>();
//        final AtomicInteger n = new AtomicInteger(0);
        final int timesPerThread = TIMES / THREAD_NUM;
        for (int i = 0; i < THREAD_NUM; i++) {
            tasks[i] = new FutureTask<Long[]>(() -> {
                Long[] rets = new Long[timesPerThread];
                for (int j = 0; j < timesPerThread; j++) {
                    rets[j] = idService.next();
//                    logger.info("id[{}]: {}", j, rets[j]);
//                    n.getAndIncrement();
                }
                countDownLatch.countDown();
                return rets;
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
}
