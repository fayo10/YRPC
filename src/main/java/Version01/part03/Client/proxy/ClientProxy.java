package Version01.part03.Client.proxy;

import lombok.AllArgsConstructor;
import Version01.part03.Client.rpcClient.RpcClient;
import Version01.part03.Client.rpcClient.impl.NettyRpcClient;
import Version01.part03.common.Message.RpcRequest;
import Version01.part03.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/// **
// * 代理类
// * 它假装是具体的业务服务实现类，但实际上它在背后偷偷帮你把请求发到了远程服务器，并把结果拿回来
// * 实现了 InvocationHandler 接口
// * 这是 JDK 动态代理的核心接口。当一个对象实现了这个接口，并且被设置为代理对象的“处理器”时，
// * 所有对该代理对象的方法调用，都不会直接执行，而是会被拦截并转发到这个类中的 invoke 方法。
// */
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    //传入参数service接口的class对象，反射封装成一个request
//RPCClientProxy类中需要加入一个RPCClient类变量即可， 传入不同的client(simple,netty), 即可调用公共的接口sendRequest发送请求
    private RpcClient rpcClient;

    //选择 Netty 客户端，并且不用传参
    public ClientProxy(){
        this.rpcClient = new NettyRpcClient();
    }

//    public ClientProxy(String host,int port,int choose){
//        switch (choose){
//            case 0:
//                rpcClient=new NettyRpcClient(host,port);
//                break;
//            case 1:
//                rpcClient=new SimpleSocketRpcClient(host,port);
//        }
//    }
//    public ClientProxy(String host,int port){
//        rpcClient=new NettyRpcClient(host,port);
//    }
    /**
     * proxy: 代理对象本身（通常用不到）。
     * method: 你刚才调用的那个方法对象（例如 getUser 方法的反射对象）。通过它可以知道你要调哪个方法、参数类型是什么。
     * args: 你传入的具体参数
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.构建request -- 封装请求
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())////服务类名,即userservice所在的包名
                .methodName(method.getName())//调用的方法名
                .params(args)//参数列表
                .paramsType(method.getParameterTypes())//参数类型,获取参数类型的 Class 数组。
                .build();

        //2.与服务端进行通信，将请求发送出去，并接受Rpcresponse响应
        RpcResponse response = rpcClient.sendRequest(request);

        //3.获取返回的结果，返回给调用者
        return response.getData();
    }

    /**
     * 是整个 RPC 客户端的“入口”和“工厂”。它的核心任务只有一个：
     *  凭空变出一个实现了指定接口的对象（代理对象），
     *  但这个对象背后没有任何业务逻辑，所有的逻辑都指向了 ClientProxy 类中的 invoke 方法。
     *
     *  客户端这样调用：UserService service = clientProxy.getProxy(UserService.class);
     * @param clazz
     * @return
     * @param <T>
     */
    //动态生成一个实现指定接口的代理对象。
    public <T>T getProxy(Class<T> clazz){
        //使用 Proxy.newProxyInstance 方法动态创建一个代理对象，传入 类加载器 、 需要代理的接口 和 调用处理程序。
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}