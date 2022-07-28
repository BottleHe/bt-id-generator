package work.bottle.plugin;

import work.bottle.plugin.exception.MachineNumOutOfBoundsException;

public class SimpleIdService implements IdService {

    private IdConverter idConverter;
    private IdGenerator idGenerator;
    private int machineNum;

    public SimpleIdService(int machineNum) {
        this(machineNum, new HighSwallowIdGenerator(), DefaultIdConverter.CURRENT);
    }

    public SimpleIdService(int machineNum, IdGenerator idGenerator) {
        this(machineNum, idGenerator, DefaultIdConverter.CURRENT);
    }

    public SimpleIdService(int machineNum, IdGenerator idGenerator, IdConverter idConverter) {
        if (0 < machineNum && 0 == (machineNum & Invariant.BT_MACHINE_NUMBER_MASK)) {
            throw new MachineNumOutOfBoundsException();
        }
        this.idConverter = idConverter;
        this.idGenerator = idGenerator;
        this.machineNum = machineNum;
    }

    @Override
    public long next() {
        return idConverter.convert(idGenerator.generateId(machineNum));
    }
}
