package work.bottle.plugin;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class SimpleIdServiceTest {

    @Test
    public void highSwallowSimpleTest() {
        for (int j = 0; j < 10; j++) {
            SimpleIdService simpleIdService = new SimpleIdService(j);
            for (int i = 0; i < 10; i++) {
                long next = simpleIdService.next();
                System.out.println(next);
                DefaultIdConverter defaultIdConverter =  DefaultIdConverter.CURRENT;
                System.out.println(defaultIdConverter.convert(next));
            }
        }
    }

    @Test
    public void highPrecisionSimpleTest() {
        HighPrecisionIdGenerator highPrecisionIdGenerator = new HighPrecisionIdGenerator(0);
        for (int j = 0; j < 10; j++) {
            SimpleIdService simpleIdService = new SimpleIdService(j, highPrecisionIdGenerator);
            for (int i = 0; i < 10; i++) {
                long next = simpleIdService.next();
                System.out.println(next);
                DefaultIdConverter defaultIdConverter = DefaultIdConverter.CURRENT;
                System.out.println(defaultIdConverter.convert(next));
            }
        }
    }

    @Test
    public void highSwallowTest() {

    }

    @Test
    public void highPrecisionTest() {

    }

}
