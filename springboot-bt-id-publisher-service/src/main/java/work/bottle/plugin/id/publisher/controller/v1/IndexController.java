package work.bottle.plugin.id.publisher.controller.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.bottle.plugin.HighSwallowIdService;
import work.bottle.plugin.IdService;

import java.lang.reflect.Field;
import java.util.List;

@RequestMapping("/index/v1")
@RestController
public class IndexController {

    private final IdService idService;

    public IndexController(IdService idService) {
        this.idService = idService;
    }

    @GetMapping("/hostname")
    public String hostname() {
        return System.getenv("HOSTNAME");
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/machineNum")
    public Integer machineNum() throws NoSuchFieldException, IllegalAccessException {
        Class<HighSwallowIdService> highSwallowIdServiceClass = (Class<HighSwallowIdService>) idService.getClass();
        Field machineNum = highSwallowIdServiceClass.getDeclaredField("machineNum");
        machineNum.setAccessible(true);
        return (int) machineNum.get(idService);
    }

    @GetMapping("/next")
    public Long next() {
        return idService.next();
    }

    @GetMapping("/next/{n}")
    public List<Long> next(@PathVariable("n") int n) {
        return idService.next(n);
    }
}
