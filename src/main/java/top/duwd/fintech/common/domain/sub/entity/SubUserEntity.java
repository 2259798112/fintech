package top.duwd.fintech.common.domain.sub.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_sub_user")
public class SubUserEntity {
    @Id
    private String userId;// varchar(32) NOT NULL,
    private Integer type;// int(11) DEFAULT NULL,
    private String tel;// varchar(50) DEFAULT NULL,
    private String weixin;// varchar(255) DEFAULT NULL,
    private String password;// varchar(255) DEFAULT NULL,
    private Integer valid;//1 有效 ， 0 无效
    private Date createTime;// datetime DEFAULT NULL,
    private Date updateTime;// datetime DEFAULT NULL,
}
