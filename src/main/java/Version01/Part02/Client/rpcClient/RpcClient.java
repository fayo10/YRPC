package Version01.Part02.Client.rpcClient;

import Version01.Part02.common.Message.RpcRequest;
import Version01.Part02.common.Message.RpcResponse;

public interface RpcClient {
    //共性抽取出来，定义底层通信的方法
    RpcResponse sendRequest(RpcRequest request);
}
