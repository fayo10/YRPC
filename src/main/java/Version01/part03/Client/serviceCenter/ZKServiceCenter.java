package Version01.part03.Client.serviceCenter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 利用 Apache Zookeeper 作为注册中心，实现了 RPC 框架中的服务发现功能
 * 当客户端需要调用某个服务时，去 Zookeeper 里查找该服务当前可用的服务器地址（IP:端口）
 */
public class ZKServiceCenter implements ServiceCenter{

    // curator 提供的zookeeper客户端
    //这是 Apache Curator 框架的核心客户端对象。Curator 是 Zookeeper 的高级客户端封装，
    // 比原生 ZK API 更易用，提供了自动重连、重试机制等特性。
    private CuratorFramework client;
    //3.指定zookeeper中定义的根节点
    private static final String ROOT_PATH = "MyRPC";

    public ZKServiceCenter(){
        //2.指数回退重试策略，用于在连接失败时，进行自动重试
        //初始重试间隔为 1000 毫秒（1 秒）。
        //最大重试次数为 3 次。
        //这种策略会使得每次重试的时间间隔呈指数增长，即：第一次重试等待 1 秒，第二次重试等待 2 秒，第三次重试等待 4 秒。
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(1000, 3);
        //1.构造器，初始化与连接，只要new了对象，连接就自动建立了
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")//zk服务器地址，本地+默认端口
                .sessionTimeoutMs(40000)//会话超时时间,表示如果客户端 40 秒没有任何活动，Zookeeper 会认为客户端失联
                .retryPolicy(policy)//重试策略,当连接失败时，应该按照怎样的方式进行重试。
                .namespace(ROOT_PATH)// 设置根命名空间,表示所有操作都将作用在 MyRPC 目录下，这样可以把不同服务的节点隔离开来，避免混淆。
                .build();
        this.client.start();//异步启动客户端连接
        System.out.println("zookeeper 连接成功");
    }

    //根据服务名（接口名）返回地址
    //将选中的字符串解析成 Java 网络编程用的 InetSocketAddress 对象
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //客户端向 Zookeeper 发送请求,获取服务名对应路径下的所有子节点，子节点通常保存服务实例的地址（ip:port 格式）。
            List<String> strings = client.getChildren().forPath("/" + serviceName);
            //先默认使用第一个，后面加负载均衡
            String string = strings.get(0);
            //节点字符串 解析为 要求格式
            return parseAddress(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //字符串 解析为 网络地址对象
    private InetSocketAddress parseAddress(String address){
        // 1. 切割字符串
        // 假设传入的 address 是 "192.168.1.10:8080"
        // split(":") 会按照冒号把它切分成一个数组
        // result[0] -> "192.168.1.10" (IP部分)
        // result[1] -> "8080" (端口部分，此时还是字符串)
        String[] result = address.split(":");
        //InetSocketAddress(String hostname, int port)
        return new InetSocketAddress(result[0],Integer.parseInt(result[1]));
    }

    //再加一个 网络地址对象 解析为 字符串（ip:port）的方法
    //服务提供者（Service Provider） 注册服务时必须用到的方法
    private String getServiceAddress(InetSocketAddress serverAddress){
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }
}
