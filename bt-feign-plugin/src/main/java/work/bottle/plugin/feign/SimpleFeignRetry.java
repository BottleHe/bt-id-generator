package work.bottle.plugin.feign;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SimpleFeignRetry extends Retryer.Default {
    private static final Logger logger = LoggerFactory.getLogger(SimpleFeignRetry.class);

    public SimpleFeignRetry() {
        this(100, TimeUnit.SECONDS.toMillis(1), 3);
    }

    public SimpleFeignRetry(long period, long maxPeriod, int maxAttempts) {
        super(period, maxPeriod, maxAttempts);
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (logger.isDebugEnabled()) {
            logger.debug("Retry, URL is {}", e.request().url());
        }
        super.continueOrPropagate(e);
    }

    @Override
    public Retryer clone() {
        return new SimpleFeignRetry();
    }
}
