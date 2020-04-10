package top.duwd.fintech.common.domain.baidu.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_baidu_cookie")
public class BaiduCookieEntity {
    @Id
    private Integer id;//` int(11) unsigned NOT NULL AUTO_INCREMENT,
    private String cookie;//` varchar(100) NOT NULL DEFAULT '',
    private Date createTime;//` datetime NOT NULL,
}
