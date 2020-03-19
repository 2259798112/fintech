package top.duwd.fintech.sc.zhihu.model.entity;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

@Data
public class ZhihuAnswerEntity {
     @Id
     private Integer id;
     private Integer qid;
     private String authorName;//"木子淇",
     private String authorImg;//"https://pic1.zhimg.com/v2-be9add353ac5a628a1ef7a762ef141b6_is.jpg",
     private String authorUrl;//"https://www.zhihu.com/people/lbj520",
     private String answerUrl;//"https://www.zhihu.com/question/26440561/answer/596672138",
     private Integer upvoteCount;//"73663",
     private Integer wordCount;//19643,
     private Integer imgCount;//34,
     private Date dateCreated;//"2019-02-13T04:00:48.000Z",
     private Date dateModified;//"2020-02-27T03:52:15.000Z",
     private Integer commentCount;//"1407",
     private Integer reward;//0
}
