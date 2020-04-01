package top.duwd.fintech.common.domain;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_book")
public class BookEntity {
    @Id
    private Integer id;// int(11) unsigned NOT NULL AUTO_INCREMENT,
    private String bookName;// varchar(255) NOT NULL DEFAULT '',
    private String bookNameRaw;// varchar(255) NOT NULL DEFAULT '',
    private String bookAuthor;// varchar(255) NOT NULL DEFAULT '',
    private String bookAuthorTranslate;// varchar(255) NOT NULL DEFAULT '',
    private String bookType;// varchar(255) DEFAULT NULL,
    private String bookPress;// varchar(255) DEFAULT NULL,
    private String jdLink;// varchar(255) DEFAULT NULL,
    private String jdLinkUnionLong;// varchar(255) DEFAULT NULL,
    private String jdLinkUnionShort;// varchar(255) DEFAULT NULL,
    private String tbLink;// varchar(255) DEFAULT NULL,
    private String tbLinkUnionLong;// varchar(255) DEFAULT NULL,
    private String tbLinkUnionShort;// varchar(255) DEFAULT NULL,
    private Date createTime;// datetime NOT NULL,
    private Date updateTime;// datetime NOT NULL,
    private String ext;// varchar(10000) DEFAULT NULL,
}
