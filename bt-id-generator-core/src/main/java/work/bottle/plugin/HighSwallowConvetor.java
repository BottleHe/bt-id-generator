package work.bottle.plugin;

public class HighSwallowConvetor implements IdConverter {
    @Override
    public long convert(Id id) {
        return ((id.getTimestamp() & Invariant.BT_HIGH_SWALLOW_TS_MASK) << 30)
                | ((id.getSequence() & Invariant.BT_HIGH_SWALLOW_SEQ_MASK) << 7)
                | (id.getMachineNum() & Invariant.BT_MACHINE_NUMBER_MASK);
    }

    @Override
    public Id convert(long id) {
        Id idObj = new Id();
        idObj.setType(Invariant.BT_HIGH_SWALLOW);
        idObj.setMachineNum((int)(id & Invariant.BT_MACHINE_NUMBER_MASK));
        idObj.setSequence((int)((id >>> 7) & Invariant.BT_HIGH_PRECISION_SEQ_MASK));
        idObj.setTimestamp((id >>> 30) & Invariant.BT_HIGH_PRECISION_TS_MASK);

        return idObj;
    }
}
