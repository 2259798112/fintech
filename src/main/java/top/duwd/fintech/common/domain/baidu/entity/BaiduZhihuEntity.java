package top.duwd.fintech.common.domain.baidu.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_baidu_zhihu")
public class BaiduZhihuEntity {
    @Id
    private Integer id;

    private String keywordMain;
    private String keywords;
    private String title;
    private String linkRaw;
    private String linkRawMd;
    private String linkReal;

    private Date createTime;
    private Date updateTime;

    private Integer answered;
    private Date answeredTime;

}
