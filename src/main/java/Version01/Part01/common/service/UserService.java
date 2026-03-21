package Version01.Part01.common.service;

import Version01.Part01.common.pojo.User;

/**
 * 定义 客户端 要调用 服务端 的服务接口
 * ✅ 客户端：通过接口获取代理对象
 * ✅ 服务端：实现这个接口
 */
public interface UserService {
    //功能一： 根据id查询用户
    User getUserByUserId(Integer id);

    //功能二： 新增用户信息(返回的是用户id)
    Integer insertUserId(User user);
}
