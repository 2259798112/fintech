package top.duwd.fintech.sc.baidu.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/baidu")
public class BaiduView {
    @GetMapping("/index")
    public String index(){
        return "baidu/baidu";
    }
}
