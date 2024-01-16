package work.bottle.plugin.controller.v1;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.bottle.plugin.id.consumer.client.IdService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/index/v1")
public class IndexController {

    private final IdService idService;

    public IndexController(IdService idService) {
        this.idService = idService;
    }

    @GetMapping("/index")
    public Long index() {
        return this.idService.next();
    }
}
