package top.duwd.fintech.sc.zhihu.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.sc.zhihu.ZhihuAnswerService;
import top.duwd.fintech.sc.zhihu.ZhihuPeopleService;
import top.duwd.fintech.sc.zhihu.ZhihuQuestionService;
import top.duwd.fintech.sc.zhihu.model.entity.ZhihuQuestionAnswerPageEntity;

@Slf4j
@RestController
@RequestMapping("/zhihu")
public class ZhihuController {
    @Autowired
    private ApiResultManager apm;
    @Autowired
    private ZhihuQuestionService zhihuQuestionService;
    @Autowired
    private ZhihuAnswerService zhihuAnswerService;
    @Autowired
    private ZhihuPeopleService zhihuPeopleService;

    @GetMapping(value = "/question/add")
    public ApiResult question(@RequestParam Integer id) {
        int count = zhihuQuestionService.parse(id);
        return apm.success(count);
    }

    @PostMapping(value = "/question/add/page", consumes = "application/json", produces = "application/json")
    public ApiResult question(@RequestBody String json) {
        int count =0;
        count = zhihuQuestionService.parseQuestion(count,JSONObject.parseObject(json));
        log.info(json);
        return apm.success(count);
    }

    @PostMapping(value = "/answer/add/page", consumes = "application/json", produces = "application/json")
    public ApiResult answer(@RequestBody ZhihuQuestionAnswerPageEntity answer) {
        int save = zhihuAnswerService.save(answer);
        return apm.success(save);
    }


    @PostMapping(value = "/people")
    public ApiResult people() {
        return apm.success();
    }
}
