package top.duwd.fintech.sc.zhihu.model.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "t_zhihu_question")
public class ZhihuQuestionEntity {
    @Id
    private Integer id;

    private String url;
    private String title;
    private Integer followers;
    private Integer views;
    private Integer questionComment;
    private String topics;
    private String similarQuestionsList;
}
