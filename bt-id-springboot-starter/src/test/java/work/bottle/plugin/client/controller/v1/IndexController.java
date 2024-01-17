package work.bottle.plugin.client.controller.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.bottle.plugin.id.consumer.client.IdService;

@RestController
@RequestMapping("/index/v1")
public class IndexController {

    private final IdService idService;

    public IndexController(IdService idService) {
        this.idService = idService;
    }

    @GetMapping("/next")
    public Long index() {
        return this.idService.next();
    }
}
