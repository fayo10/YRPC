package Version01.Part02.Client.rpcClient.impl;

import Version01.Part02.Client.netty.nettyInitializer.NettyClientInitializer;
import Version01.Part02.Client.rpcClient.RpcClient;
import Version01.Part02.common.Message.RpcRequest;
import Version01.Part02.common.Message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;

/**
 * 是 Java NIO (Non-blocking I/O) + Netty 框架 实现，属于“现代工业”阶段，代码复杂但性能极强，支持高并发和异步处理。
 */
@AllArgsConstructor
public class NettyRpcClient implements RpcClient {
    private String host;
    private int port;

    private static final Bootstrap bootstrap;//是 Netty 用于启动客户端的对象，负责设置与服务器的连接配置。
    private static final EventLoopGroup eventLoopGroup;//是 Netty 的线程池，用于处理 I/O 操作。

    //初始化 netty 客户端
    static {
        eventLoopGroup = new NioEventLoopGroup();//默认创建 CPU 核心数 * 2 个线程，负责处理所有连接的 IO 事件（读/写）。
        bootstrap = new Bootstrap();//Netty 客户端的启动引导类，用于配置连接参数。
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)//指定使用 NIO 模式的 Socket 通道（非阻塞 TCP）。
                .handler(new NettyClientInitializer());//指定之前分析过的初始化器
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            //发起异步连接请求
            //1.建立连接
            //connect发送异步连接请求，此时连接可能还没建立成功。
            //.sync()：关键点！ 这是一个阻塞操作。
            //当前线程（业务线程）会停在这里，直到 TCP 三次握手完成（连接成功）或者发生异常。
            //如果连接失败，这里会抛出异常。
            //效果：将 Netty 的异步连接变成了同步连接。
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //2.获取建立好的通道
            Channel channel = channelFuture.channel();//代表了你和服务器之间那条具体的 TCP 连接,类似socket

            //3.发送请求
            //write(request)：将 RpcRequest 对象写入出站缓冲区。
            //此时会触发 Pipeline 中的出站流程：
            // ObjectEncoder (序列化) -> LengthFieldPrepender (加长度头) -> 写入 Socket 缓冲区。
            //flush()：强制将缓冲区的数据真正发送到网络上。
            channel.writeAndFlush(request);
            // ↑ 这里会经过 Pipeline 处理，netty初始化

            //阻塞等待响应完成
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 当前场景下选择堵塞获取结果
            // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...

            //提取结果
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse rpcResponse = channel.attr(key).get();

            return rpcResponse;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
