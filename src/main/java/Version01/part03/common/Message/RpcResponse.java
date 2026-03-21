package Version01.part03.common.Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 *封装响应，成功返回数据（调用方法的返回值），失败返回状态码告知前端
 */
@Data
@Builder
public class RpcResponse implements Serializable {
    //状态信息
    private int code;
    private String msg;
    //具体数据
    private  Object data;

    //成功信息
    public static RpcResponse susees(Object data){
        RpcResponse rpcResponse = RpcResponse.builder()
                .code(200)
                .msg("成功")
                .data(data)
                .build();
        return rpcResponse;
    }

    //失败信心
    public static RpcResponse fail(){
        RpcResponse rpcResponse = RpcResponse.builder()
                .code(500)
                .msg("服务失败")
                .build();
        return rpcResponse;
    }
}
