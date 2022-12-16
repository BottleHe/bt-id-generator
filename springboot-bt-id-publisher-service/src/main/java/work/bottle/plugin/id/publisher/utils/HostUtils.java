package work.bottle.plugin.id.publisher.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class HostUtils {
    public static final Set<String> HOST_NAME_LIST = new HashSet<>();

    public static void loadHostNameList() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            Enumeration<NetworkInterface> subInterfaces = networkInterface.getSubInterfaces();
            while (subInterfaces.hasMoreElements()) {
                NetworkInterface subNetworkInterface = subInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses1 = subNetworkInterface.getInetAddresses();
                while (inetAddresses1.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses1.nextElement();
                    HOST_NAME_LIST.add(inetAddress.getHostAddress());
                    HOST_NAME_LIST.add(inetAddress.getHostName());
                    HOST_NAME_LIST.add(inetAddress.getCanonicalHostName());
                }
            }
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                HOST_NAME_LIST.add(inetAddress.getHostAddress());
                HOST_NAME_LIST.add(inetAddress.getHostName());
                HOST_NAME_LIST.add(inetAddress.getCanonicalHostName());
            }
        }
    }

    public static Set<String> getHostNameList() {
        return HOST_NAME_LIST;
    }
}
