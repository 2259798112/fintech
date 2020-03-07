package top.duwd.fintech.coin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.coin.huobi.HuobiApiUtil;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.coin.service.BigOrderService;
import top.duwd.fintech.common.domain.BigOrderEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/big")
@Slf4j
public class BigOrderController {

    @Autowired
    private BigOrderService bigOrderService;
    @Autowired
    private HuobiApiUtil huobiApiUtil;
    @Autowired
    private RequestBuilder requestBuilder;

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

        List<BigOrderEntity> list = bigOrderService.list(start, end, plat, min);
        return new ApiResultManager().success(list);
    }

    @GetMapping("/list/detail")
    public ApiResult listDetail(@RequestParam String plat, @RequestParam Date start, @RequestParam Date end, @RequestParam Integer min, Integer last) {
        //获取 时间
        List<Date> kDate = DateUtil.getCandleDate(start, end, last);
        int size = kDate.size();
        double[] volumes = new double[size - 1];
        //获取 对应的K线
        List<BigOrderEntity> list = bigOrderService.list(start, end, plat, min,1000000);
        for (int i = 0; i < size - 1; i++) {
            for (BigOrderEntity entity : list) {
                Date ts = entity.getTs();
                String side = entity.getSide();
                Double amount = entity.getAmount();
                if (ts.getTime() >= kDate.get(i).getTime() && ts.getTime() < kDate.get(i + 1).getTime()){ //有效
                    if ("sell".equalsIgnoreCase(side)){
                        volumes[i] = volumes[i] - amount;
                    }else {
                        volumes[i] = volumes[i] + amount;
                    }
                }
            }
        }
        String period = null;
        if (last <=60){
            period = last +"min";
        }else if (last == 240){
            period = "4hour";
        }else if (last == 1440){
            period = "1day";
        }
        List<CandleModel> kList = huobiApiUtil.getKList(requestBuilder, HuobiApiUtil.BTC_CQ, period, null, kDate.get(0), kDate.get(size - 2));
        List<double[]> datas = new ArrayList<>();
        for (CandleModel candleModel : kList) {
            double[] ds = new double[5];
            ds[0]=candleModel.getOpen();
            ds[1]=candleModel.getClose();
            ds[2]=candleModel.getLow();
            ds[3]=candleModel.getHigh();
            ds[4]=candleModel.getAmount();
            datas.add(ds);
        }
        kDate.remove(size - 1);

        List<String> dates = new ArrayList<>(kDate.size());
        for (Date date : kDate) {
            dates.add(JSON.toJSONString(date,SerializerFeature.WriteDateUseDateFormat).replaceAll("\"",""));
        }
        K k = new K();
        k.setDates(dates);
        k.setData(datas);
        k.setVolumes(volumes);

        return new ApiResultManager().success(k);
    }


    @Data
    class K {
        private List<String> dates;
        private List<double[]> data; //[[17512.58,17633.11,17434.27,17642.81,86160000]] 开收低高量
        private double[] volumes; //
    }

}
