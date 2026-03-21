package Version01.part03.Client;

import Version01.part03.Client.proxy.ClientProxy;
import Version01.part03.common.pojo.User;
import Version01.part03.common.service.UserService;

/**
 * 客户端 测试类
 */
public class TestClient {
    public static void main(String[] args) {
        //1.创建 代理类，连接到指定的服务器和端口
        //创建代理对象时，不用从客户端这传入端口、地址等信息了
        ClientProxy clientProxy = new ClientProxy();
        ////内部改为通过动态服务发现机制（如 Zookeeper）获取服务端地址
        UserService proxy = clientProxy.getProxy(UserService.class);

//        //2.获得UserService代理对象
//        UserService userService = proxy.getProxy(UserService.class);

        //3.使用代理对象 调用方法
        //4.服务器处理完请求后，返回数据，客户端获取并输出数据
        User user = proxy.getUserByUserId(1);//JDK 会调用 ClientProxy.invoke()
        System.out.println("从服务端得到的user="+user.toString());

        User u= User.builder().id(100).userName("wxx").sex(true).build();
        Integer id = proxy.insertUserId(u);
        System.out.println("向服务端插入user的id"+id);
    }

}
