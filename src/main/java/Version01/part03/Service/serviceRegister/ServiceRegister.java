package Version01.part03.Service.serviceRegister;

import java.net.InetSocketAddress;

//服务注册接口
public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serviceAddress);
}
