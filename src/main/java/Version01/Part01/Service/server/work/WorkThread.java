package Version01.Part01.Service.server.work;

import Version01.Part01.Service.provider.ServiceProvider;
import Version01.Part01.common.Message.RpcRequest;
import Version01.Part01.common.Message.RpcResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 实现了 Runnable 接口，用于处理客户端请求并返回响应。
 * 其核心功能是在多线程环境中接收来自客户端的请求，调用本地服务，并将服务的结果返回给客户端。
 */
@AllArgsConstructor
public class WorkThread implements Runnable {

    private Socket socket;//建立网络连接
    private ServiceProvider serviceProvider;

    /**
     * 接收输入流中客户端发送过来的请求，调用本地服务后，将结果序列化到response到输出流发动给客户端
     */
    @Override
    public void run() {
        try {
            //接收request
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcRequest request = (RpcRequest) objectInputStream.readObject();

            //反射调用服务方法获取返回的信息
            RpcResponse response = getResponse(request);

            //将信息写入输出流给客户端
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        } catch (IOException |ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理客户端请求的实际业务逻辑
     * @param request
     * @return
     */
    private RpcResponse getResponse(RpcRequest request){
        //1.从请求中获取 服务名，从服务注册中心的映射表中获取 实例（xxximpl）
        String interfaceName = request.getInterfaceName();//com.service.UserService
        //2.获取实例
        Object service = serviceProvider.getService(interfaceName);//userServiceImpl

        try {
            //3.通过反射调用方法，将结果封装并返回
            //利用反射，在 service 对象的所有方法中，精准找到名字叫 getUser 且参数是 String 的那个方法对象 (Method)
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsType());
            //method.invoke(对象, 参数): 这是反射的终极指令。
            // 在 service 这个对象上，执行刚才找到的 method 方法，传入真实的参数值。
            Object invoke = method.invoke(service, request.getParams());
            return RpcResponse.susees(invoke);
        } catch (NoSuchMethodException |IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }

    }
}
