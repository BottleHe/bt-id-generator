package work.bottle.plugin.id.consumer.client.config;

import java.util.List;

public class IdPublisherProperties {
    private List<String> serviceDomainList;

    public List<String> getServiceDomainList() {
        return serviceDomainList;
    }

    public void setServiceDomainList(List<String> serviceDomainList) {
        this.serviceDomainList = serviceDomainList;
    }
}
