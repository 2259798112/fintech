package top.duwd.fintech.sc.zhihu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.sc.zhihu.ZhihuQuestionService;
import top.duwd.fintech.sc.zhihu.model.vo.ZhihuQuestionVo;

@Slf4j
@RestController
@RequestMapping("/zhihu")
public class ZhihuController {
    @Autowired
    private ApiResultManager apm;
    @Autowired
    private ZhihuQuestionService zhihuQuestionService;

    @PostMapping(value = "/question/add", consumes = "application/json", produces = "application/json")
    public ApiResult question(@RequestBody ZhihuQuestionVo vo) {
        int save = zhihuQuestionService.save(vo);
        return apm.success(save);
    }


    @PostMapping(value = "/people")
    public ApiResult people() {

        return apm.success();
    }
}
