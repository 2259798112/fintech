package top.duwd.fintech.sc.zhihu.model.vo;

import lombok.Data;

@Data
public class SimilarQuestionDto {
    private String title;
    private String url;
    private Integer answerCount;
}
