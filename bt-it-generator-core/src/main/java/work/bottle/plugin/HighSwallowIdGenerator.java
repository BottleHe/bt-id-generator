package work.bottle.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.exception.SystemTimeException;
import work.bottle.plugin.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class HighSwallowIdGenerator implements IdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(HighSwallowIdGenerator.class);
    private volatile long lastTimestamp = 0l;
    private volatile int sequence = 0;

    private ReentrantLock lock = new ReentrantLock();

    private final int machineNum;

    public HighSwallowIdGenerator(int machineNum) {
        this.machineNum = machineNum;
    }

    @Override
    public Id generateId() {
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

    @Override
    public List<Id> generateId(int n) {
        Id id = null;
        List<Id> retList = new ArrayList<>();
        try {
            lock.lock();
            for (int i = 0; i < n; i++) {
                id = new Id();
                id.setType(Invariant.BT_HIGH_SWALLOW);
                id.setMachineNum(machineNum & Invariant.BT_MACHINE_NUMBER_MASK);
                long ts = TimeUtils.getTimeSeconds();
                validateTimestamp(ts);
                if (ts == lastTimestamp) {
                    if (0l == (sequence & Invariant.BT_HIGH_SWALLOW_SEQ_MASK)) {
                        // 序列号越界了
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
                retList.add(id);
            }
            return retList;
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
