package work.bottle.plugin;

import work.bottle.plugin.exception.MachineNumOutOfBoundsException;

import java.util.ArrayList;
import java.util.List;

public class HighPrecisionIdService implements IdService {
    private static final IdConverter idConverter = new HighPrecisionConvertor();
    private static final IdPopulator idPopulator = new HighPrecisionIdPopulator();
    private int machineNum;

    public HighPrecisionIdService(int machineNum) {
        if (0 < machineNum && 0 == (machineNum & Invariant.BT_MACHINE_NUMBER_MASK)) {
            throw new MachineNumOutOfBoundsException();
        }
        this.machineNum = machineNum;
    }

    @Override
    public long next() {
        Id id = new Id(Invariant.BT_HIGH_PRECISION, machineNum);
        idPopulator.populate(id);
        return idConverter.convert(id);
    }

    @Override
    public List<Long> next(int n) {
        n = 1000 < n || 0 > n ? 1000 : n;
        List<Id> ids = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ids.add(new Id(Invariant.BT_HIGH_PRECISION, machineNum));
        }
        idPopulator.populate(ids);
        List<Long> list = new ArrayList<>();
        ids.forEach(id -> list.add(idConverter.convert(id)));
        return list;
    }
}
