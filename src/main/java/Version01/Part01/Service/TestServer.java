package Version01.Part01.Service;

import Version01.Part01.Service.provider.ServiceProvider;
import Version01.Part01.Service.server.RpcServer;
import Version01.Part01.Service.server.impl.SimpleRPCServer;
import Version01.Part01.common.service.impl.UserserviceImpl;

public class TestServer {
    public static void main(String[] args) {
        //创建实例，注册服务
        UserserviceImpl userservice = new UserserviceImpl();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userservice);
        //实例化服务器
        RpcServer rpcServer = new SimpleRPCServer(serviceProvider);
        //启动服务端
        rpcServer.start(9999);
    }
}
