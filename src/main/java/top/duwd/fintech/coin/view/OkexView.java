package top.duwd.fintech.coin.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/coin/okex")
@Controller
public class OkexView {

    @GetMapping(value = "/index")
    public String index() {
        return "page/ok";
    }
}
