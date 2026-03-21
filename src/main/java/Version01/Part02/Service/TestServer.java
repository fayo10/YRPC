package Version01.Part02.Service;

import Version01.Part02.Service.provider.ServiceProvider;
import Version01.Part02.Service.server.RpcServer;
import Version01.Part02.Service.server.impl.NettyRPCRPCServer;
import Version01.Part02.common.service.impl.UserserviceImpl;

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

        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.provideServiceInterface(userservice);

        RpcServer rpcServer=new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
