package work.bottle.plugin;

import java.util.List;

/**
 * 负责生成ID的服务
 */
public interface IdService {

    /**
     * 计算出下一个ID
     * @return
     */
    public long next();

    /**
     * 计算出下n个id
     * @param n
     * @return
     */
    public List<Long> next(int n);
}
