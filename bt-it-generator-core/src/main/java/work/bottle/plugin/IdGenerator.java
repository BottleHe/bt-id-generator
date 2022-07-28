package work.bottle.plugin;

/**
 * ID生成器抽象定义
 */
public interface IdGenerator {

    /**
     * 获取ID
     * @return 生成的ID
     */
    public Id generateId(int machineNum);
}
