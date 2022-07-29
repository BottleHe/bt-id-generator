package work.bottle.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.exception.SystemTimeException;
import work.bottle.plugin.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class HighSwallowIdPopulator implements IdPopulator {
    private static final Logger logger = LoggerFactory.getLogger(HighSwallowIdPopulator.class);
    private volatile long lastTimestamp = 0l;
    private volatile int sequence = 0;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void populate(Id id) {
        try {
            lock.lock();
            long ts = TimeUtils.getTimeSeconds();
            validateTimestamp(ts);
            if (ts == lastTimestamp) {
                if (0l == (sequence & Invariant.BT_HIGH_SWALLOW_SEQ_MASK)) {
                    // 时间越界了
                    if (logger.isDebugEnabled()) {
                        logger.debug("序列号越界[sequence = {}], 需要等到下一个时间点.", sequence);
                    }
                    ts = waitFotNextTick(ts);
                    sequence = 0;
                    lastTimestamp = ts;
                }
            } else {
                sequence = 0;
                lastTimestamp = ts;
            }
            id.setTimestamp(ts);
            id.setSequence(sequence++);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void populate(List<Id> ids) {
        try {
            lock.lock();
            for (Id id : ids) {
                long ts = TimeUtils.getTimeSeconds();
                validateTimestamp(ts);
                if (ts == lastTimestamp) {
                    if (0l == (sequence & Invariant.BT_HIGH_SWALLOW_SEQ_MASK)) {
                        // 序列号越界了
                        if (logger.isDebugEnabled()) {
                            logger.debug("序列号越界[sequence = {}], 需要等到下一个时间点.", sequence);
                        }
                        ts = waitFotNextTick(ts);
                        sequence = 0;
                        lastTimestamp = ts;
                    }
                } else {
                    sequence = 0;
                    lastTimestamp = ts;
                }
                id.setTimestamp(ts);
                id.setSequence(sequence++);
            }
        } finally {
            lock.unlock();
        }
    }

    public long waitFotNextTick(long ts) {
        while (ts <= lastTimestamp) {
            ts = TimeUtils.getTimeSeconds();
        }
        return ts;
    }

    public void validateTimestamp(long ts) {
        if (ts < lastTimestamp) {
            throw new SystemTimeException();
        }
    }
}
