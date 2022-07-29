package work.bottle.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.exception.SystemTimeException;
import work.bottle.plugin.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class HighPrecisionIdGenerator implements IdGenerator{
    private static final Logger logger = LoggerFactory.getLogger(HighPrecisionIdGenerator.class);
    private volatile long lastTimestamp = 0l;
    private volatile int sequence = 0;

    private ReentrantLock lock = new ReentrantLock();

    private final int machineNum;

    public HighPrecisionIdGenerator(int machineNum) {
        this.machineNum = machineNum;
    }

    @Override
    public Id generateId() {
        Id id = new Id();
        id.setType(Invariant.BT_HIGH_PRECISION);
        id.setMachineNum(machineNum & Invariant.BT_MACHINE_NUMBER_MASK);
        try {
            lock.lock();
            long ts = TimeUtils.getTimeMilliSeconds();
            validateTimestamp(ts);
            if (ts == lastTimestamp) {
                if (0l == (sequence & Invariant.BT_HIGH_PRECISION_SEQ_MASK)) {
                    // 同一时间序列号越界了
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
            id = new Id();
            id.setType(Invariant.BT_HIGH_PRECISION);
            id.setMachineNum(machineNum & Invariant.BT_MACHINE_NUMBER_MASK);
            for (int i = 0; i < n; i++) {
                long ts = TimeUtils.getTimeMilliSeconds();
                validateTimestamp(ts);
                if (ts == lastTimestamp) {
                    if (0l == (sequence & Invariant.BT_HIGH_PRECISION_SEQ_MASK)) {
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
                retList.add(id);
            }

            return retList;
        } finally {
            lock.unlock();
        }
    }

    public long waitFotNextTick(long ts) {
        while (ts <= lastTimestamp) {
            ts = TimeUtils.getTimeMilliSeconds();
        }
        return ts;
    }

    public void validateTimestamp(long ts) {
        if (ts < lastTimestamp) {
            throw new SystemTimeException();
        }
    }
}
