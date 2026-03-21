package Version01.Part01.Client;

import Version01.Part01.Client.proxy.ClientProxy;
import Version01.Part01.common.pojo.User;
import Version01.Part01.common.service.UserService;

/**
 * 客户端 测试类
 */
public class TestClient {
    public static void main(String[] args) {
        //1.创建 代理类，连接到指定的服务器和端口
        ClientProxy proxy = new ClientProxy("127.0.0.1", 9999);

        //2.获得UserService代理对象
        UserService userService = proxy.getProxy(UserService.class);

        //3.使用代理对象 调用方法
        //4.服务器处理完请求后，返回数据，客户端获取并输出数据
        User user = userService.getUserByUserId(1);//JDK 会调用 ClientProxy.invoke()
        System.out.println("从服务端得到的user="+user.toString());

        User u=User.builder().id(100).userName("wxx").sex(true).build();
        Integer id = userService.insertUserId(u);
        System.out.println("向服务端插入user的id"+id);
    }

}
