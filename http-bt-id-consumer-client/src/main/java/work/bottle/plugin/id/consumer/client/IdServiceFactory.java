package work.bottle.plugin.id.consumer.client;

import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.bottle.plugin.id.consumer.client.config.IdPublisherProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static feign.Util.checkNotNull;
import static feign.Util.emptyToNull;

public class IdServiceFactory {

    private static class LBTarget<T> implements Target<T> {
        private final Class<T> type;
        private final String name;
        private final List<String> urlList;

        private volatile int n = 0;

        public LBTarget(Class<T> type, List<String> urlList) {
            this(type, "default", urlList);
        }

        public LBTarget(Class<T> type, String name, List<String> urlList) {
            this.type = checkNotNull(type, "type");
            this.name = checkNotNull(emptyToNull(name), "name");
            urlList = null == urlList || urlList.isEmpty() ? null : urlList;
            this.urlList = checkNotNull(urlList, "urlList");
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public synchronized String url() {
            n =  n < urlList.size() ? n : 0;
            return urlList.get(n++);
        }

        /* no authentication or other special activity. just insert the url. */
        @Override
        public Request apply(RequestTemplate input) {
            input.target(url()); // 通过我们的url方法, 更新使用下一个可用的URL
            return input.request();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LBTarget) {
                LBTarget<?> other = (LBTarget) obj;
                return type.equals(other.type)
                        && name.equals(other.name)
                        && urlList.equals(other.urlList);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + type.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + urlList.hashCode();
            return result;
        }

        @Override
        public String toString() {
            if (name.equals("default")) {
                return "LBTarget(type=" + type.getSimpleName() + ", urlList=" + urlList + ")";
            }
            return "LBTarget(type=" + type.getSimpleName() + ", name=" + name + ", urlList=" + urlList
                    + ")";
        }
    }

    private static class SimpleFeignRetry extends Retryer.Default {
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

    public static IdService getIdServer(IdPublisherProperties idPublisherProperties) {
        return Feign.builder().client(new OkHttpClient())
                .retryer(new SimpleFeignRetry())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(new LBTarget<IdService>(IdService.class, idPublisherProperties.getServiceDomainList()));
    }
}
