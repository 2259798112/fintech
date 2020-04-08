package top.duwd.fintech.common.domain;

import lombok.Data;

import java.util.Map;

@Data
public class BaiduZhihuDto {
    private String keyword;
    private Map<String, String> urls;
}
