package top.duwd.fintech.coin.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/trade")
@Controller
public class TradeRecordView {

    @GetMapping(value = "/record")
    public String index() {
        return "TradeRecord";
    }
}
