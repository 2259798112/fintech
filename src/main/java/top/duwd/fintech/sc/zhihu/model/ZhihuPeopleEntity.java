package top.duwd.fintech.sc.zhihu.model;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

@Data
public class ZhihuPeopleEntity {
    @Id
    private String uid;//lu-ha-hua
    private String name;//鲁哈花
    private String url;//https://www.zhihu.com/people/lu-ha-hua
    private String img;//https://pic3.zhimg.com/ecfa3a89c645daad632756187ea1c4e4_is.jpg
    private String headline;//进步使人难过
    private String education;//
    private String company;//

    private Integer following;//关注人
    private Integer followers;//粉丝
    private Integer zan;//获赞总数
    private Integer like;//喜欢
    private Integer fav;//收藏

    private Integer answers;//回答数
    private Integer videos;//视频数
    private Integer asks;//提问数
    private Integer posts;//文章数
    private Integer columns;//专栏
    private Integer pins;//想法

    private Date createDate;
    private Date updateDate;

}
