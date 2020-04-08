package top.duwd.fintech.sc.baidu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.common.domain.baidu.dto.BaiduZhihuDto;
import top.duwd.fintech.sc.baidu.service.BaiduService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;
    @Autowired
    private ApiResultManager apm;

    @PostMapping(value = "/parse", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult parse(@RequestBody String json) {

        JSONObject jsonObject = JSON.parseObject(json);
        List<String> keywords = jsonObject.getJSONArray("keywords").toJavaList(String.class);
        String keywordMain = jsonObject.getString("keywordMain");
        List<BaiduZhihuDto> listRaw = baiduService.parse(keywords,keywordMain);
        baiduService.parseZhihuLink(listRaw);

        List<BaiduZhihuDto> notEmptyList = baiduService.filterNotEmptyList(keywordMain, listRaw);
        log.info("finish");
        return apm.success(notEmptyList);
    }
}
