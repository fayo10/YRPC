package Version01.Part02.Service.server.impl;

import Version01.Part01.Service.provider.ServiceProvider;
import Version01.Part01.Service.server.RpcServer;
import Version01.Part01.Service.server.work.WorkThread;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 实现了 RpcServer 接口，用于启动一个简单的 RPC 服务器，
 * 并监听客户端的连接请求，处理客户端请求，并通过线程并发处理每个连接。
 */
@AllArgsConstructor
public class SimpleRPCServer implements RpcServer {

    private ServiceProvider serviceProvide;//本地注册中心

    @Override
    public void start(int port) {
        try {
            //创建一个 ServerSocket 实例，用于在指定的 port 端口上监听客户端的连接请求
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动了");
            //服务器持续接受客户端的连接请求
            while (true) {
                //如果没有连接，会堵塞在这里
                Socket socket = serverSocket.accept();
                //有连接，创建一个新的线程执行处理
                new Thread(new WorkThread(socket,serviceProvide)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        //停止服务端
        //可以在未来版本中优化服务端关闭的流程。
    }
}
