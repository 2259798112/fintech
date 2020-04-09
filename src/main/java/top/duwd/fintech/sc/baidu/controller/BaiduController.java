package top.duwd.fintech.sc.baidu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.common.domain.baidu.entity.BaiduZhihuEntity;
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

    @GetMapping(value = "/list")
    public ApiResult list(String keywordMain,Integer answered,Integer updated,Boolean like) {

        List<BaiduZhihuEntity> list = baiduService.findListByKV("keywordMain", keywordMain,like);
        List<BaiduZhihuEntity> listFilter  = baiduService.filterList(list,answered,updated);

        return apm.success(listFilter);
    }
    @GetMapping(value = "/list/update")
    public ApiResult list(Integer id){

        int i = baiduService.updateAnsweredById(id);
        return apm.success(i);
    }

    @PostMapping(value = "/search/keyword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult searchKeyword(@RequestBody String json) {

        JSONObject jsonObject = JSON.parseObject(json);
        List<String> keywords = jsonObject.getJSONArray("keywords").toJavaList(String.class);
        String keywordMain = jsonObject.getString("keywordMain");
        String cookie = jsonObject.getString("cookie");
        if (!StringUtils.isEmpty(cookie)){
            BaiduService.hMap.put("Cookie", cookie);
        }

        List<BaiduZhihuEntity> listRaw = baiduService.searchKeyword(keywords,keywordMain);
        baiduService.parseZhihuLink(listRaw);
        List<BaiduZhihuEntity> list = baiduService.saveList(keywordMain, listRaw);
        return apm.success(list);
    }
}
