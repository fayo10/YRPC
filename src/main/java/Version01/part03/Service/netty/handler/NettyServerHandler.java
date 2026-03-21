package Version01.part03.Service.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import Version01.part03.Service.provider.ServiceProvider;
import Version01.part03.common.Message.RpcRequest;
import Version01.part03.common.Message.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 利用 Netty 接收请求，利用 Java 反射机制“动态”地执行了客户端指定的代码
 * 业务处理
 */
@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ServiceProvider serviceProvider;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        //接收request，读取并调用服务
        RpcResponse response = getResponse(request);
        //将结果传递给客户端
        channelHandlerContext.writeAndFlush(response);
        //关闭当前连接
        channelHandlerContext.close();
    }

    //接收request，读取并调用服务的具体实现方法
    private RpcResponse getResponse(RpcRequest request) {
        //得到服务名
        String interfaceName = request.getInterfaceName();
        //得到对应的实例对象(根据服务名到 服务注册中心 维护的 “接口名”到“实现类实例”的映射表 找对应的实现类)
        Object service = serviceProvider.getService(interfaceName);
        //反射调用方法
        try {
            //根据实例对象，找到对应的UserServiceImpl 这个类的类对象，再找对应的方法
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsType());
            // 在 service 这个对象上，执行刚才找到的 method 方法，传入真实的参数值。
            Object invoke = method.invoke(service, request.getParams());
            return RpcResponse.susees(invoke);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }
    }
}
