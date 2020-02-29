package top.duwd.fintech.coin.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/big")
@Controller
public class BigView {

    @GetMapping(value = "/index")
    public String index() {
        return "big";
    }
}
