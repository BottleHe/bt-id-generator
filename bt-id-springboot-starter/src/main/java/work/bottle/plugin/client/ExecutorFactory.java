package work.bottle.plugin.client;

import java.util.concurrent.ExecutorService;

public interface ExecutorFactory {

    public ExecutorService getExecutor();
}
