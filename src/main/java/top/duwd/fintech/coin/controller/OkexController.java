package top.duwd.fintech.coin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.indicator.KDJ;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.dutil.common.model.KDJModel;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/okex")
@RestController
public class OkexController {
    @Autowired
    private OkexApiUtil okexApiUtil;
    @Autowired
    private ApiResultManager apm;


    @GetMapping("/kdj/contact/{symbol}/{period}/{high}/{low}/{diff}")
    public ApiResult kdjFilter(@PathVariable String symbol, @PathVariable Integer period
            , @PathVariable Integer diff
            , @PathVariable Integer high
            , @PathVariable Integer low) {

        List<CandleModel> candleList = okexApiUtil.getCandleList(symbol, Integer.toString(period * 60), null, null);
        KDJModel[] kdjModels = KDJ.kdjDefault(candleList, false);
        ArrayList<CandleModel> lowList = new ArrayList<CandleModel>();
        ArrayList<CandleModel> highList = new ArrayList<CandleModel>();

        for (int i = 0; i < kdjModels.length; i++) {
            CandleModel candleModel = new CandleModel();
            int reIndex = kdjModels.length - 1 - i;
            BeanUtils.copyProperties(candleList.get(reIndex), candleModel);
            KDJModel kdjModel = kdjModels[i];
            Date date = candleModel.getDataDate();
            String time = JSON.toJSONString(date, SerializerFeature.WriteDateUseDateFormat);
            String timeKDJ = time + " k=" + kdjModel.getK() + " d=" + kdjModel.getD() + " j=" + kdjModel.getJ();
            candleModel.setDataTime(timeKDJ);

            if (kdjModel.getD() < low && kdjModel.getK() < low && Math.abs(kdjModel.getK() - kdjModel.getD()) < diff) {
                lowList.add(candleModel);
            }

            if (kdjModel.getD() > high && kdjModel.getK() > high && Math.abs(kdjModel.getK() - kdjModel.getD()) < diff) {
                highList.add(candleModel);
            }
        }

        HashMap<String, List<CandleModel>> map = new HashMap<>();
        map.put("low", lowList);
        map.put("high", highList);

        return apm.success(map);
    }
}
