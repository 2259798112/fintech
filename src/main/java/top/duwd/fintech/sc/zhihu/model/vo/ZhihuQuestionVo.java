package top.duwd.fintech.sc.zhihu.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ZhihuQuestionVo {
    private String url;
    private String title;
    private Integer followers;
    private Integer views;
    private Integer questionComment;
    private List<String> topics;
    private List<SimilarQuestionDto> similarQuestionsList;
}
