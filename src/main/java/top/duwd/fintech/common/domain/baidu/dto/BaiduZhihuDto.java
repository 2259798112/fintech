package top.duwd.fintech.common.domain.baidu.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BaiduZhihuDto {
    private String keywordMain;
    private String keywords;
    private Map<String, String> urls;
}
