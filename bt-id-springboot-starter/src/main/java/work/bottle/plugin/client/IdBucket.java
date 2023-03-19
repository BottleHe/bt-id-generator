package work.bottle.plugin.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import work.bottle.plugin.id.consumer.client.IdService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ID 缓存桶
 */
public class IdBucket implements IdService {
    private static final Logger logger = LoggerFactory.getLogger(IdBucket.class);
    private static final int CAPACITY = 2000;
    private static final int LAW_WATER = 300;

    /**
     * 线程池, 可以使用外部传入的线程池
     */
    private final Executor executor;
    /**
     * ID容器
     */
    private final LinkedList<Long> idQueue = new LinkedList<Long>();

    /**
     * 锁列表
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 扩容来源, 这里是使用的一个远程Service
     */
    private final IdService sourceIdService;

    public IdBucket(IdService sourceIdService, @Nullable Executor executor) {
        this.executor = null == executor ? Executors.newFixedThreadPool(1) : executor;
        this.sourceIdService = sourceIdService;
        populatorIdBucket();
    }

    /**
     * 异步填充ID缓存
     */
    private void populatorIdBucket() {
        executor.execute(this::populatorIdSafe);
    }

    /**
     * 安全的同步填充ID缓存, 一般是在非执行线程中执行时需要.
     */
    public void populatorIdSafe() {
        // 数据填充
        if (LAW_WATER > idQueue.size()) {
            IdBucket.this.lock.lock();
            try {
                if (LAW_WATER > IdBucket.this.idQueue.size()) {
                    populatorId();
                }
            } finally {
                IdBucket.this.lock.unlock();
            }
        }
    }

    /**
     * 无锁的填充队列操作.
     * @return int 填充的ID数量
     */
    public int populatorId() {
        int n = 0;
        do {
            int p = CAPACITY - idQueue.size() > 1000 ? 1000 : CAPACITY - idQueue.size();
            List<Long> next = IdBucket.this.sourceIdService.next(p);
            n += next.size();
            next.forEach(idQueue::addLast);
        } while (idQueue.size() < CAPACITY);
        return n;
    }

    /**
     * 从缓存中取出一个ID并返回.
     * @return long ID
     */
    @Override
    public Long next() {
        Long aLong = -1L;
        this.lock.lock();
        try {
            do {
                try {
                    aLong = this.idQueue.removeFirst();
                } catch (NoSuchElementException e) {
                    logger.warn("IdQueue is empty, will populator some data.");
                    populatorId(); // 同步填充
                }
            } while (0 >= aLong);
        } finally {
            this.lock.unlock();
        }
        // 异步填充
        populatorIdBucket();
        return aLong;
    }

    /**
     * 从缓存中取n个请求, 这里限制死了. 0 < n <= 10
     * @param n 0 < n <= 10
     * @return 返回一个ID列表, 最多10个
     */
    @Override
    public List<Long> next(int n) {
        if (0 >= n) {
            n = 1;
        } else if (10 < n) {
            n = 10;
        }
        List<Long> ret = new ArrayList<>(n);
        for (int i = 0; i < 10; ++i) {
            ret.add(next());
        }
        return ret;
    }
}
