package top.duwd.fintech.common.domain.zhihu.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "t_zhihu_book")
public class ZhihuBookEntity {
    @Id
    private String id;// varchar(64) NOT NULL DEFAULT '' COMMENT '书名+qid Hash',
    private String bookNameAnswer;// varchar(255) NOT NULL DEFAULT '', 回答中提到的书名
    private String authorIconList;// text, 回答者头像
    private String authorNameList;// text, 回答者昵称
    private String authorAllIconList;// text, 所有回答者（除了书名号包括的）
    private String authorAllNameist;// text,
    private Integer linkBookId;// int(11) DEFAULT NULL, 关联的书id
    private Integer zhihuQuestionId;// int(11) NOT NULL, 问题id
    private String md;// int(11) NOT NULL, 全字段md
}
