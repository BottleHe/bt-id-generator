package work.bottle.plugin;

import work.bottle.plugin.utils.TimeUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * ID 的结构化表示
 * 转化依赖具体实现
 */
public class Id {
    // 类型
    private int type;
    // 时间戳
    private long timestamp;
    // 序列号
    private int sequence;

    // 机器号( 可能再做细分 )
    private int machineNum;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getMachineNum() {
        return machineNum;
    }

    public void setMachineNum(int machineNum) {
        this.machineNum = machineNum;
    }

    public LocalDateTime getDateTime() {
        return Invariant.BT_HIGH_PRECISION == type
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp + TimeUtils.EPOCH), ZoneId.of("+8"))
                : LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp * 1000 + TimeUtils.EPOCH), ZoneId.of("+8"));
    }

    @Override
    public String toString() {
        return "Id{" +
                "type=" + type +
                ", timestamp=" + timestamp + "(" + getDateTime() + ")" +
                ", sequence=" + sequence +
                ", machineNum=" + machineNum +
                '}';
    }
}
