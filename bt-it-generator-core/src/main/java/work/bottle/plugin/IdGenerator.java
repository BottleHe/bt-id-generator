package work.bottle.plugin;

import java.util.List;

/**
 * ID生成器抽象定义
 */
public interface IdGenerator {

    /**
     * 获取ID
     * @return 生成的ID
     */
    public Id generateId();

    /**
     * 批量生成ID的方法
     * @param n
     * @return
     */
    public List<Id> generateId(int n);
}
