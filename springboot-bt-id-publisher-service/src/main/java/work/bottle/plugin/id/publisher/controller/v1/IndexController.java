package work.bottle.plugin.id.publisher.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.bottle.plugin.HighSwallowIdService;
import work.bottle.plugin.IdService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.List;

@RequestMapping("/index/v1")
@RestController
public class IndexController {

    private IdService idService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Autowired
    public void setIdService(IdService idService) {
        this.idService = idService;
    }

    @GetMapping("/hostname")
    public String hostname() {
        return System.getenv("HOSTNAME");
    }

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
    public List<Long> next(@PathVariable int n) {
        return idService.next(n);
    }
}
