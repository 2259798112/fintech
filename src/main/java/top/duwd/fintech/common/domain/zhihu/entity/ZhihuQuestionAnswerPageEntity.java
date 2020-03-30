package top.duwd.fintech.common.domain.zhihu.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_zhihu_question_answer_page")
public class ZhihuQuestionAnswerPageEntity {
    @Id
    private Integer id;//` int(11) NOT NULL COMMENT '问题id:1045099692',
    private Integer upvoteNum;//` int(11) NOT NULL DEFAULT '0' COMMENT '赞同数字4871',
    private Integer commentNum;//` int(11) NOT NULL DEFAULT '0' COMMENT '评论数字350',
    private Integer textCount;//` int(11) DEFAULT '0' COMMENT '文字数',
    private Integer imgCount;//` int(11) DEFAULT '0' COMMENT '图片数',
    private Integer reward;//` int(11) DEFAULT '0' COMMENT '是否有赞赏按钮',
    private Date dateCreated;//` datetime NOT NULL COMMENT '创建时间',
    private Date dateModified;//` datetime NOT NULL COMMENT '修改时间',
    private String authorId;//` varchar(255) NOT NULL COMMENT '作者id',
    private String authorName;//` varchar(255) NOT NULL COMMENT '作者昵称',
    private String authorUrl;//` varchar(255) NOT NULL COMMENT '作者主页',
    private String authorImage;//` varchar(255) NOT NULL COMMENT '作者头像url',
    private String authorHeadline;//` varchar(255) DEFAULT NULL COMMENT '作者简介',
    private Integer authorFollowerCount;//` int(11) NOT NULL DEFAULT '0' COMMENT '作者粉丝',
    private Integer questionId;//` int(11) DEFAULT NULL COMMENT '问题id',
    private String questionTitle;//` varchar(255) DEFAULT NULL COMMENT '问题标题',
    private String content;

}
