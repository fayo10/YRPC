package Version01.part03.Client.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import Version01.part03.common.Message.RpcResponse;

/**
 * 是 Netty 中用于处理服务器端响应的处理器
 * 它是一个入站处理器（Inbound Handler），专门负责处理从网络通道（Channel）中读取到的、类型为 RpcResponse 的数据。
 * 它是 Netty RPC 客户端的响应接收器。它负责把网络上传输过来的二进制流还原成 Java 对象 (RpcResponse)，
 * 并通过 Channel 属性机制“通知”发送方数据已到达，最后关闭连接。
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    //这是 SimpleChannelInboundHandler 的核心方法，用于读取服务端返回的数据。

    /**
     * ctx：上下文对象，包含 Channel、Pipeline 等信息，用于操作连接。
     * @param channelHandlerContext
     * @param rpcResponse
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        //AttributeKey<RpcResponse>:这是一个泛型类，代表一个唯一的标识符（Key）。
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
        //.channel(): 获取当前正在处理数据的网络连接通道（Channel）对象。
        //“找到当前的网络连接，用 'RPCResponse' 这把钥匙打开它的属性夹层，把服务器返回的结果对象存进去。”
        //目的：让其他地方（通常是发送请求的代码）能通过同一个 Channel 和同一个 Key 把这个结果取出来。
        channelHandlerContext.channel().attr(key).set(rpcResponse);
        //“数据已经拿到了（存到 Attribute 里了），现在的任务完成了，把这个网络连接关掉，释放资源。”
        channelHandlerContext.channel().close();//关闭连接
    }

    //用于捕获运行过程中出现的异常，进行处理并释放资源。
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }


}
