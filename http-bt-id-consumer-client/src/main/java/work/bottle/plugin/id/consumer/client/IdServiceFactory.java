package work.bottle.plugin.id.consumer.client;

import feign.Feign;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import work.bottle.plugin.feign.LBTarget;
import work.bottle.plugin.id.consumer.client.config.IdPublisherProperties;

public class IdServiceFactory {

    public static IdService getIdServer(IdPublisherProperties idPublisherProperties) {
        return Feign.builder().client(new OkHttpClient())
                .retryer(Retryer.NEVER_RETRY)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(new LBTarget<IdService>(IdService.class, idPublisherProperties.getServiceDomainList()));
    }
}
