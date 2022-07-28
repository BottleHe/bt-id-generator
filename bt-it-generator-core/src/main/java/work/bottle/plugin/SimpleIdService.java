package work.bottle.plugin;

import work.bottle.plugin.exception.MachineNumOutOfBoundsException;

import java.util.ArrayList;
import java.util.List;

public class SimpleIdService implements IdService {

    private IdConverter idConverter;
    private IdGenerator idGenerator;
    private int machineNum;

    public SimpleIdService(int machineNum) {
        this(machineNum, new HighSwallowIdGenerator(machineNum), DefaultIdConverter.CURRENT);
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
        return idConverter.convert(idGenerator.generateId());
    }

    @Override
    public List<Long> next(int n) {
        n = 100 < n || 0 > n ? 100 : n;
        List<Id> ids = idGenerator.generateId(n);
        List<Long> list = new ArrayList<>();
        ids.forEach(id -> list.add(idConverter.convert(id)));
        return list;
    }
}
