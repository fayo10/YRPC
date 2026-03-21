package Version01.part03.Service.server.impl;

import Version01.Part01.Service.provider.ServiceProvider;
import Version01.Part01.Service.server.RpcServer;
import Version01.Part01.Service.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 是一个实现了 RpcServer 接口的 RPC 服务器，它通过 线程池 来管理和执行请求处理任务，以提高并发处理能力。
 * 相比于 SimpleRPCRPCServer，这个类通过使用线程池来管理工作线程，可以更有效地处理大量并发请求，
 * 并避免每个请求都创建一个新的线程导致性能问题。
 */
public class ThreadPoolRPCServer implements RpcServer {
    private final ThreadPoolExecutor threadPool;//定义一个线程池对象 threadPool，用于管理和执行线程任务
    private ServiceProvider serviceProvider;

    //默认构造方法：创建一个线程池，核心线程数等于 CPU 核心数，最大线程数 1000，非核心线程空闲存活时间60秒，队列大小为 100。
    public ThreadPoolRPCServer(ServiceProvider serviceProvider){
        threadPool=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000,60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));
        this.serviceProvider= serviceProvider;
    }
    //自定义构造方法：允许用户传入线程池参数，自定义线程池配置。
    public ThreadPoolRPCServer(ServiceProvider serviceProvider, int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue){

        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.serviceProvider = serviceProvider;
    }
    @Override
    public void start(int port) {
        System.out.println("服务端启动了");
        try {
            ServerSocket serverSocket = new ServerSocket();
            while (true){
                Socket socket = serverSocket.accept();
                threadPool.execute(new WorkThread(socket,serviceProvider));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
