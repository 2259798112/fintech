package top.duwd.fintech.coin.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.coin.service.BigOrderService;
import top.duwd.fintech.common.domain.BigOrderEntity;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/big")
public class BigOrderController {

    @Autowired
    private BigOrderService bigOrderService;

    @GetMapping("/cal")
    public ApiResult cal(@RequestParam String plat, @RequestParam Date start, @RequestParam Date end, @RequestParam Integer min,Integer last) {
        if (last !=null && last > 0){
            end = new Date();
            start = DateUtil.addMin(end,-last);
        }
        JSONObject cal = bigOrderService.cal(start, end, plat, min);
        return new ApiResultManager().success(cal);
    }

    @GetMapping("/list")
    public ApiResult list(@RequestParam String plat, @RequestParam Date start, @RequestParam Date end, @RequestParam Integer min,Integer last) {
        if (last !=null && last > 0){
            end = new Date();
            start = DateUtil.addMin(end,-last);
        }

        List<BigOrderEntity> list = bigOrderService.list(start, end, plat, min);
        return new ApiResultManager().success(list);
    }

}
