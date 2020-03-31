package top.duwd.fintech.common.domain.zhihu.vo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "t_zhihu_book")
public class ZhihuBookVo {
    @Id
    private String id;// varchar(64) NOT NULL DEFAULT '' COMMENT '书名+qid Hash',
    private String bookNameAnswer;// varchar(255) NOT NULL DEFAULT '', 回答中提到的书名
    private List<String> authorIconList;// text, 回答者头像
    private List<String> authorNameList;// text, 回答者昵称
    private List<String> authorAllIconList;// text, 所有回答者（除了书名号包括的）
    private List<String> authorAllNameList;// text,
    private Integer linkBookId;// int(11) DEFAULT NULL, 关联的书id
    private Integer zhihuQuestionId;// int(11) NOT NULL, 问题id
    private String md;// int(11) NOT NULL, 全字段md
    private Integer valid;// int(11) NOT NULL, 全字段md, 默认1 小
    private Date createTime;
    private Date updateTime;
}
