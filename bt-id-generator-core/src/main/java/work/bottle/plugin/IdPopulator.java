package work.bottle.plugin;

import java.util.List;

public interface IdPopulator {

    public void populate(Id id);

    public void populate(List<Id> ids);
}
