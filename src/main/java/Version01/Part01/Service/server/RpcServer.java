package Version01.Part01.Service.server;

/**
 * 定义 RPC 服务器的接口
 * 方便以后扩展不同的实现（BIO、NIO、Netty 等）
 */
public interface RpcServer {

    void start(int port);
    void stop();
}
