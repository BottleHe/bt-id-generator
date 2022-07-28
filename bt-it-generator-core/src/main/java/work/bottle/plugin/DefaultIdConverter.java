package work.bottle.plugin;

public class DefaultIdConverter implements IdConverter {

    public static final DefaultIdConverter CURRENT = new DefaultIdConverter();

    private DefaultIdConverter(){}

    @Override
    public long convert(Id id) {
        if (Invariant.BT_HIGH_PRECISION == id.getType()) {
            return 0x4000000000000000L
                    | ((id.getTimestamp() & Invariant.BT_HIGH_PRECISION_TS_MASK) << 20)
                    | ((id.getSequence() & Invariant.BT_HIGH_PRECISION_SEQ_MASK) << 7)
                    | (id.getMachineNum() & Invariant.BT_MACHINE_NUMBER_MASK);
        } else {
            return ((id.getTimestamp() & Invariant.BT_HIGH_SWALLOW_TS_MASK) << 30)
                    | ((id.getSequence() & Invariant.BT_HIGH_SWALLOW_SEQ_MASK) << 7)
                    | (id.getMachineNum() & Invariant.BT_MACHINE_NUMBER_MASK);
        }
    }

    @Override
    public Id convert(long id) {
        Id idObj = new Id();
        if (0 < (id & 0x4000000000000000L)) { // High Precision
            idObj.setType(Invariant.BT_HIGH_PRECISION);
            idObj.setMachineNum((int)(id & Invariant.BT_MACHINE_NUMBER_MASK));
            idObj.setSequence((int)((id >>> 7) & Invariant.BT_HIGH_PRECISION_SEQ_MASK));
            idObj.setTimestamp((id >>> 20) & Invariant.BT_HIGH_PRECISION_TS_MASK);
        } else {
            idObj.setType(Invariant.BT_HIGH_SWALLOW);
            idObj.setMachineNum((int)(id & Invariant.BT_MACHINE_NUMBER_MASK));
            idObj.setSequence((int)((id >>> 7) & Invariant.BT_HIGH_PRECISION_SEQ_MASK));
            idObj.setTimestamp((id >>> 30) & Invariant.BT_HIGH_PRECISION_TS_MASK);
        }
        return idObj;
    }
}
