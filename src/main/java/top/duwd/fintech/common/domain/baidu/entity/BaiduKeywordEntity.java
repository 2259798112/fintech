package top.duwd.fintech.common.domain.baidu.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_baidu_keyword")
public class BaiduKeywordEntity {
    @Id
    private Integer id;//` int(11) unsigned NOT NULL AUTO_INCREMENT,
    private String main;//` varchar(100) NOT NULL DEFAULT '',
    private String keyword;//` varchar(512) NOT NULL DEFAULT '',
    private Date createTime;//` datetime NOT NULL,
    private Date updateTime;//` datetime NOT NULL,
    private Integer searchCount;//` int(11) NOT NULL DEFAULT '0',
}
