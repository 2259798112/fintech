package top.duwd.fintech.common.domain.sub.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_sub_question")
public class SubQuestionEntity {
    @Id
    private Integer id;// int(11) NOT NULL,
    private String userId;// varchar(255) NOT NULL COMMENT '用户id',
    private Integer questionId;// int(11) NOT NULL COMMENT '知乎问题id',
    private Integer valid;// int(255) NOT NULL,

    private Date createTime;// datetime NOT NULL,
    private Date updateTime;// datetime NOT NULL,
}
