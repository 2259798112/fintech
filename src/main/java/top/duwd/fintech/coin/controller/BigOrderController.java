package top.duwd.fintech.coin.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/big")
public class BigOrderController {

    @Autowired
    private BigOrderService bigOrderService;

    @GetMapping("/cal")
    public ApiResult cal(@RequestParam String plat, @RequestParam Date start, @RequestParam Date end, @RequestParam Integer min, Integer last) {
//        if (last != null && last > 0) {
//            end = new Date();
//            start = DateUtil.addMin(end, -last);
//        }
        JSONObject cal = bigOrderService.cal(start, end, plat, min);
        return new ApiResultManager().success(cal);
    }

    @GetMapping("/list")
    public ApiResult list(@RequestParam String plat, @RequestParam Date start, @RequestParam Date end, @RequestParam Integer min, Integer last) {
//        if (last != null && last > 0) {
//            end = new Date();
//            start = DateUtil.addMin(end, -last);
//        }

        List<BigOrderEntity> list = bigOrderService.list(start, end, plat, min);
        return new ApiResultManager().success(list);
    }

    @GetMapping("/list/detail")
    public K listDetail(@RequestParam String plat, @RequestParam Date start, @RequestParam Date end, @RequestParam Integer min, Integer last) {

        List<BigOrderEntity> list = bigOrderService.list(start, end, plat, min);
        List<Date> kDate = DateUtil.getCandleDate(start, end, last);
        Double[] buyList = new Double[kDate.size() - 1];
        Double[] sellList = new Double[kDate.size() - 1];


        for (int i = 0; i < kDate.size() - 1; i++) {
            System.out.println(i);
            for (BigOrderEntity entity : list) {
                if (entity.getTs().getTime() > kDate.get(i + 1).getTime() || entity.getTs().getTime() < kDate.get(i).getTime()) continue;

                if (entity.getTs().getTime() >= kDate.get(i).getTime() && entity.getTs().getTime() < kDate.get(i + 1).getTime()) {
                    if (entity.getSide().equalsIgnoreCase("buy")) {

                        if (buyList[i] == null || buyList[i] == 0.0d) {
                            buyList[i] = entity.getAmount();
                        } else {
                            buyList[i] = entity.getAmount() + buyList[i];
                        }
                    } else {
                        if (sellList[i] == null || sellList[i] == 0.0d) {
                            sellList[i] = entity.getAmount();
                        } else {
                            sellList[i] = entity.getAmount() + sellList[i];
                        }
                    }
                }


            }
        }

        K k = new K();
        k.setBuyList(Arrays.asList(buyList));
        k.setSellList(Arrays.asList(sellList));
        k.setDateList(kDate);
        return k;
    }

    @Data
    class K {
        private List<Double> buyList;
        private List<Double> sellList;
        private List<Date> dateList;
    }

}
