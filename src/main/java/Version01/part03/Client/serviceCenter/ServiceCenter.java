package Version01.part03.Client.serviceCenter;

import java.net.InetSocketAddress;

//服务中心
public interface ServiceCenter {
    //InetSocketAddress 是 Java 中的一个类，属于 java.net 包。
    // 它表示了一个网络地址（包含 IP 地址和端口号），通常用于在网络中标识一个计算机的端口。
    //  查询：根据服务名查找地址
    InetSocketAddress serviceDiscovery(String serviceName);
}
