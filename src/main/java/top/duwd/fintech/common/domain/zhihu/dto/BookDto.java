package top.duwd.fintech.common.domain.zhihu.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookDto {
    private String bookName;

    private List<String> authorIconUrl;// 书名号 回答者 头像
    private List<String> authorAnswerUrl;// 书名号 回答者 回答url

    private List<String> authorAnotherIconUrl;
    private List<String> authorAnswerAnotherUrl;

    private Integer db;
    private Integer link;
    private Integer linkBookId;
}
