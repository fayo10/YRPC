package Version01.Part01.Client;

import Version01.Part01.common.Message.RpcRequest;
import Version01.Part01.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 这里负责底层与服务端的通信，发送request，返回response
 * 负责底层的网络通信
 * 发送：把 RpcRequest 对象发给服务端
 * 接收：从服务端接收 RpcResponse 对象
 */
public class IOClient {
    public static RpcResponse sentRequest(String host, int port, RpcRequest request){
        //参数列表：主机IP地址、端口号、请求对象

        try {
            //1.通过socket与服务器建立 TCP 连接
            Socket socket = new Socket(host, port);

            //2.将Rpcrequest 序列化 ，通过 输出流 发送到服务端
            //socket.getOutputStream()获取输出流
            //ObjectOutputStream 是一个用于将对象序列化成字节流的类
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);//将 request 对象序列化并写入到输出流中，准备发送到服务端

            //3.刷新输出流，确保数据完全发送
            //虽然 ObjectOutputStream 会在 writeObject() 后自动进行刷新，但手动调用 flush() 可以确保数据立即发送，特别是在有多个写操作时，确保数据的即时性。
            objectOutputStream.flush();

            //4.从 输入流 中获取服务器返回来的 序列化的Rpcresponse，将其 反序列化 Ppcresponse
            //ObjectInputStream 用于从输入流中读取对象并进行反序列化
            //readObject() 方法从输入流中读取数据
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcResponse response = (RpcResponse) objectInputStream.readObject();

            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
