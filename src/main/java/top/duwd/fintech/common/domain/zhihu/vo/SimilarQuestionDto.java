package top.duwd.fintech.common.domain.zhihu.vo;

import lombok.Data;

@Data
public class SimilarQuestionDto {
    private String title;
    private String url;
    private Integer answerCount;
}
