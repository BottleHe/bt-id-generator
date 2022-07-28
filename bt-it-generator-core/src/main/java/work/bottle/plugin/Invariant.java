package work.bottle.plugin;

public final class Invariant {

    // 符号位, 固定为0. 第63位(位数从0开始)
    public static final int BT_SYMBOL_BIT = 0;

    // 高精度模式, 使用毫秒时间戳, 第62bit (位数从0开始).
    public static final int BT_HIGH_PRECISION = 1;
    // 高精度模式, 使用42bit存储毫秒级时间戳, 大约可以使用140年
    public static final long BT_HIGH_PRECISION_TS_MASK = 0x3FFFFFFFFFFL;
    // 高精度模式, 使用13bit存储序列号, 也就是最大 8192次/ms
    public static final int BT_HIGH_PRECISION_SEQ_MASK = 0x1FFF;

    // 高吞吐模式, 第62bit (位数从0开始).
    public static final int BT_HIGH_SWALLOW = 0;
    // 高吞吐模式, 使用32bit存储秒级时间戳, 大约可以使用136年
    public static final long BT_HIGH_SWALLOW_TS_MASK = 0xFFFFFFFFL;
    // 高吞吐模式, 使用23bit存储序列号, 大约830W次/s, 8300次/ms
    public static final int BT_HIGH_SWALLOW_SEQ_MASK = 0x7FFFFF;

    // 固定使用7bit存储机器号, 最多128台机器
    public static final int BT_MACHINE_NUMBER_MASK = 0x7F; // 最多128台机器
}
