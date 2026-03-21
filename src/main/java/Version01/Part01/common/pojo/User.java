package Version01.Part01.common.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户端 和 服务端 共同拥有的对象
 * 实现Serializable接口，表示该类可以序列化
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Integer id;
    private String userName;
    private Boolean sex;
}
