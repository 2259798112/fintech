package top.duwd.fintech.coin.view;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.coin.service.BigOrderService;

import java.util.Date;

@RestController
@RequestMapping("/big")
public class BigOrderController {

    @Autowired
    private BigOrderService bigOrderService;

    @GetMapping("/cal")
    public ApiResult cal(@RequestParam String plat, @RequestParam Date start, @RequestParam Date end, @RequestParam Integer min) {
        JSONObject cal = bigOrderService.cal(start, end, plat, min);
        return new ApiResultManager().success(cal);
    }

}
