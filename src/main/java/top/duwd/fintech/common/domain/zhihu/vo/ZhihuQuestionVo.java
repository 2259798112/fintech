package top.duwd.fintech.common.domain.zhihu.vo;

import lombok.Data;

import java.util.List;

@Data
public class ZhihuQuestionVo {
    private String url;
    private String title;
    private Integer followers;
    private Integer views;
    private String questionComment;
    private List<String> topics;
    private List<SimilarQuestionDto> similarQuestionsList;
}
