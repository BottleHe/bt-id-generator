package work.bottle.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.exception.SystemTimeException;
import work.bottle.plugin.utils.TimeUtils;

import java.util.concurrent.locks.ReentrantLock;

public class HighSwallowIdGenerator implements IdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(HighSwallowIdGenerator.class);
    private volatile long lastTimestamp = 0l;
    private volatile int sequence = 0;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public Id generateId(int machineNum) {
        Id id = new Id();
        id.setType(Invariant.BT_HIGH_SWALLOW);
        id.setMachineNum(machineNum & Invariant.BT_MACHINE_NUMBER_MASK);
        try {
            lock.lock();
            long ts = TimeUtils.getTimeSeconds();
            validateTimestamp(ts);
            if (ts == lastTimestamp) {
                if (0l == (ts & Invariant.BT_HIGH_SWALLOW_TS_MASK)) {
                    // 时间越界了
                    if (logger.isDebugEnabled()) {
                        logger.debug("序列号越界, 需要等到下一个时间点.");
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
            return id;
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
