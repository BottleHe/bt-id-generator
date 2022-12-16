package work.bottle.plugin.id.consumer.client;

import feign.Request;
import feign.RequestTemplate;
import feign.Target;

import java.util.List;

import static feign.Util.checkNotNull;
import static feign.Util.emptyToNull;

public class LBTarget<T> implements Target<T> {
    private final Class<T> type;
    private final String name;
    private final List<String> urlList;

    private volatile int n = 0;

    public LBTarget(Class<T> type, List<String> urlList) {
        this(type, "default", urlList);
    }

    public LBTarget(Class<T> type, String name, List<String> urlList) {
        this.type = checkNotNull(type, "type");
        this.name = checkNotNull(emptyToNull(name), "name");
        urlList = null == urlList || urlList.isEmpty() ? null : urlList;
        this.urlList = checkNotNull(urlList, "urlList");
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public synchronized String url() {
        n =  n < urlList.size() ? n : 0;
        return urlList.get(n++);
    }

    /* no authentication or other special activity. just insert the url. */
    @Override
    public Request apply(RequestTemplate input) {
        if (input.url().indexOf("http") != 0) {
            input.target(url());
        }
        return input.request();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LBTarget) {
            LBTarget<?> other = (LBTarget) obj;
            return type.equals(other.type)
                    && name.equals(other.name)
                    && urlList.equals(other.urlList);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + urlList.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (name.equals("default")) {
            return "LBTarget(type=" + type.getSimpleName() + ", urlList=" + urlList + ")";
        }
        return "LBTarget(type=" + type.getSimpleName() + ", name=" + name + ", urlList=" + urlList
                + ")";
    }
}
