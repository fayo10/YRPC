package Version01.Part01.common.service.impl;

import Version01.Part01.common.pojo.User;
import Version01.Part01.common.service.UserService;

import java.util.Random;
import java.util.UUID;

public class UserserviceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询 id：{} 的用户"+id);

        //这里模拟数据库操作
        Random random = new Random();
        User user = User.builder()
                .userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean())
                .build();
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入用户" +user.getUserName()+ "的数据");
        return user.getId();
    }
}
