package work.bottle.plugin.id.consumer.client;

import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface IdService {

    @RequestLine("GET /index/v1/next")
    public Long next();

    @RequestLine("GET /index/v1/next/{n}")
    public List<Long> next(@Param("n") int n);
}
