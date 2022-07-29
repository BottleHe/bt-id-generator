package work.bottle.plugin;

public class HighPrecisionConvertor implements IdConverter {

    @Override
    public long convert(Id id) {
        return 0x4000000000000000L
                | ((id.getTimestamp() & Invariant.BT_HIGH_PRECISION_TS_MASK) << 20)
                | ((id.getSequence() & Invariant.BT_HIGH_PRECISION_SEQ_MASK) << 7)
                | (id.getMachineNum() & Invariant.BT_MACHINE_NUMBER_MASK);
    }

    @Override
    public Id convert(long id) {
        Id idObj = new Id();
        idObj.setType(Invariant.BT_HIGH_PRECISION);
        idObj.setMachineNum((int)(id & Invariant.BT_MACHINE_NUMBER_MASK));
        idObj.setSequence((int)((id >>> 7) & Invariant.BT_HIGH_PRECISION_SEQ_MASK));
        idObj.setTimestamp((id >>> 20) & Invariant.BT_HIGH_PRECISION_TS_MASK);
        return idObj;
    }
}
