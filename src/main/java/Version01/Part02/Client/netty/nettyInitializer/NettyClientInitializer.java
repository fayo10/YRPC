package Version01.Part02.Client.netty.nettyInitializer;

import Version01.Part02.Client.netty.handler.NettyClientHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 用于初始化客户端的 Channel 和 ChannelPipeline。
 * 在 Netty 中，Channel 是网络通信的基本单元，而 ChannelPipeline 是一个用于处理消息的责任链，
 * 它包含了一系列的 ChannelHandler，每个 ChannelHandler 都负责处理不同的操作，如编码、解码、异常处理等。
 * <p>
 * 每当客户端与服务器建立一个新的 TCP 连接（创建一个 SocketChannel）时，
 * Netty 会自动调用这个类的 initChannel 方法。
 * 在这个方法里，你为这个新的连接定制了一条专属的数据处理流水线（Pipeline）。
 */
public class NettyClientInitializer extends ChannelInitializer {
    /**
     * 用于初始化每个新的 SocketChannel（即新的连接）。
     * 每个 SocketChannel 会有一个独立的 ChannelPipeline，用于定义该连接上所有数据的处理流程。
     *【出站 Outbound】从后往前执行（Tail → Head）
     * 【入站 Inbound】从前往后执行（Head → Tail）
     * @param channel
     * @throws Exception
     */
    @Override
    protected void initChannel(Channel channel) throws Exception {
        //初始化，每个 SocketChannel 都有一个独立的管道（Pipeline），用于定义数据的处理流程。
        ChannelPipeline pipeline = channel.pipeline();
        //消息格式【长度】【消息体】，解决沾包问题
        /*
        参数含义：
        10 * 1024 * 1024：允许的最大帧长度为 10MB，防止过大数据包导致内存溢出。
        0, 4：表示长度字段的起始位置和长度。
        0, 4：去掉长度字段后，计算实际数据的偏移量。
         */
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(10 * 1024 * 1024, 0, 4, 0, 4));
        //计算当前待发送消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));
        //编码器
        //使用Java序列化方式，netty的自带的解码编码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());
        //解码器
        //使用了Netty中的ObjectDecoder，它用于将字节流解码为 Java 对象。
        //在ObjectDecoder的构造函数中传入了一个ClassResolver 对象，用于解析类名并加载相应的类。
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));

        //最后把 NettyClientHandler  处理器加到容器中。
        pipeline.addLast(new NettyClientHandler());
    }
}
