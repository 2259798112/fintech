package top.duwd.fintech.sc.zhihu.model.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AnswerDto {
    private String bookName;

    private ArrayList<String> authorIconUrl;// 书名号 回答者 头像
    private ArrayList<String> authorAnswerUrl;// 书名号 回答者 回答url

    private ArrayList<String> authorAnotherIconUrl;
    private ArrayList<String> authorAnswerAnotherUrl;
}
