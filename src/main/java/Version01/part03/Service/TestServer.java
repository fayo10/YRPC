package Version01.part03.Service;

import Version01.part03.Service.provider.ServiceProvider;
import Version01.part03.Service.server.RpcServer;
import Version01.part03.Service.server.impl.NettyRPCRPCServer;
import Version01.part03.common.service.impl.UserserviceImpl;

public class TestServer {
    public static void main(String[] args) {
//        //创建实例，注册服务
//        UserserviceImpl userservice = new UserserviceImpl();
//        ServiceProvider serviceProvider = new ServiceProvider();
//        serviceProvider.provideServiceInterface(userservice);
//        //实例化服务器
//        RpcServer rpcServer = new NettyRpcServier(serviceProvider);
//        //启动服务端
//        rpcServer.start(9999);
        UserserviceImpl userservice = new UserserviceImpl();

        ServiceProvider serviceProvider=new ServiceProvider("127.0.0.1",9999);
        serviceProvider.provideServiceInterface(userservice);

        RpcServer rpcServer=new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
