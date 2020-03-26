package top.duwd.fintech.sc.zhihu.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/zhihu")
public class ZhihuView {
    @GetMapping(value = "/answer/book/list")
    public String index() {
        return "zhihu/booklist";
    }
}
