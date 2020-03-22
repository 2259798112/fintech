package top.duwd.fintech.sc.zhihu.model.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "t_zhihu_question_topic")
public class ZhihuQuestionTopicEntity {
    @Id
    private Integer id;// int(11) NOT NULL,
    private String url;// varchar(255) NOT NULL,
    private String name;// varchar(255) DEFAULT NULL,
    private String topicType;// varchar(255) DEFAULT NULL,
    private String avatarUrl;// varchar(255) DEFAULT NULL,
    private Integer qid;// int(11) NOT NULL,
}
