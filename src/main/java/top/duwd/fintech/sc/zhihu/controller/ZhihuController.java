package top.duwd.fintech.sc.zhihu.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.common.domain.zhihu.dto.AnswerDto;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuQuestionAnswerPageEntity;
import top.duwd.fintech.sc.zhihu.service.ZhihuAnswerService;
import top.duwd.fintech.sc.zhihu.service.ZhihuQuestionService;

import java.util.List;

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

    @GetMapping(value = "/question/add")
    public ApiResult question(@RequestParam Integer id) {
        log.info("/question/add");
        int count = zhihuQuestionService.parse(id);
        return apm.success(count);
    }

    /**
     * 网页 问题主页 添加问题
     * @param json
     * @return
     */
    @PostMapping(value = "/question/add/page", consumes = "application/json", produces = "application/json")
    public ApiResult questionAddPage(@RequestBody String json) {
        log.info("/question/add/page");
        int count =0;
        count = zhihuQuestionService.parseQuestion(count,JSONObject.parseObject(json));
        log.info(json);
        return apm.success(count);
    }

    /**
     * 网页 问题主页 添加答案
     * @param answer
     * @return
     */
    @PostMapping(value = "/answer/add/page", consumes = "application/json", produces = "application/json")
    public ApiResult answer(@RequestBody ZhihuQuestionAnswerPageEntity answer) {
        log.info("/answer/add/page");
        int save = zhihuAnswerService.save(answer);
        return apm.success(save);
    }

    @GetMapping(value = "/answer/book")
    public ApiResult answerBook(@RequestParam(value = "id")Integer id,int limit) {
        log.info("question id={}",id);
        List<AnswerDto> list = zhihuAnswerService.findBook(id,limit);
        return apm.success(list);
    }


}
