package Version01.part03.Service.provider;

import Version01.part03.Service.serviceRegister.Impl.ZKServiceRegister;
import Version01.part03.Service.serviceRegister.ServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务注册中心（本地版
 * 维护一张“服务类名”到“实现类实例”的映射表。当服务器收到客户端的请求
 * （比如“我要调用 UserService”）时，它就查这张表，找到对应的 UserServiceImpl 对象，然后才能执行代码。
 */
public class ServiceProvider {

    //集合中存放服务的实例,
    // Key (String): 接口的全限定名（例如 "com.example.UserService"）。这是客户端请求时携带的唯一标识。
    //Value (Object): 具体的业务实现类实例（例如 new UserServiceImpl()）。
    private Map<String,Object> interfaceProvider;
    private int port;
    private String host;
    //注册服务类
    private ServiceRegister serviceRegister;

    //构造函数
//    public ServiceProvider(){
//        this.interfaceProvider = new HashMap<>();
//    }
    public ServiceProvider(String host,int port){
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
    }

    //本地注册服务
    //传入一个对象（比如 new UserServiceImpl()）。
    //反射获取它实现了哪些接口（比如 UserService）。
    //遍历这些接口，把 "com.example.UserService" 和 userServiceImpl 对象 绑定存起来。
    public void provideServiceInterface(Object service){
        //接受一个服务实例对象（new后生成的对象），getInterfaces() 获取的是： 接口类对象 (Class<?>)
        Class<?>[] interfaces = service.getClass().getInterfaces();
        //将接口的全限定名和对应的服务的实例添加到 map 中。

        //遍历接口名时，将服务注册到注册中心
        for (Class<?> aClass : interfaces) {
            //本机的映射表
            interfaceProvider.put(aClass.getName(),service);
            //aClass.getName() 获取的是： 服务名

            //在注册中心注册服务
            serviceRegister.register(aClass.getName(),new InetSocketAddress(host,port));
        }
    }

    //获取服务实例
    public  Object getService(String interfacesName){
        return interfaceProvider.get(interfacesName);
    }
}
