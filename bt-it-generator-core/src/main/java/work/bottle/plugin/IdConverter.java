package work.bottle.plugin;

public interface IdConverter {

    /**
     * 将对象类型的ID转化为 long类型的数字
     * @param id
     * @return
     */
    public long convert(Id id);

    /**
     * 将数字类型的ID转化为 可识别的对象
     * @param id
     * @return
     */
    public Id convert(long id);
}
