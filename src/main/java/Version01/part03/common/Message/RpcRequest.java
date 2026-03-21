package Version01.part03.common.Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装客户端要调用的方法信息
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    //客户端只用知道 ：接口等信息，在服务端接口指向实现类

    //服务类名 com.example.service.UserService
    private String interfaceName;
    //调用的方法名 getUserInfo
    private String methodName;
    //参数列表
    private Object[] params;
    //参数类型
    private Class<?>[] paramsType;
}
