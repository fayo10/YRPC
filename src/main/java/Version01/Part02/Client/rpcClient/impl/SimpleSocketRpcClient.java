package Version01.Part02.Client.rpcClient.impl;

import Version01.Part02.Client.rpcClient.RpcClient;
import Version01.Part02.common.Message.RpcRequest;
import Version01.Part02.common.Message.RpcResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 这是一个简单实现的客户端，sendRequest 方法也与 IOClient 中相同。
 * 负责底层与服务端的通信，发送request，返回response
 * 是 Java BIO (Blocking I/O) 实现，属于“原始人”阶段，代码简单但性能差，无法应对高并发
 */
@AllArgsConstructor
public class SimpleSocketRpcClient implements RpcClient {

    private String host;//主机地址
    private int port;//端口号

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        //这里是客户端向服务器发送请求，获取服务器返回的信息

        try {
            //1、建立socket连接
            Socket socket = new Socket(host, port);

            //2，序列化request，通过输出流发送到服务器
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            //3.接收服务器返回的信息，对其进行反序列化，转为response
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcResponse response = (RpcResponse) objectInputStream.readObject();

            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
