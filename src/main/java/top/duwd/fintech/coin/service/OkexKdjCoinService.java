package top.duwd.fintech.coin.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.indicator.KDJ;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.dutil.common.model.KDJModel;
import top.duwd.fintech.coin.job.OkexJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OkexKdjCoinService {
    @Autowired
    private OkexApiUtil okexApiUtil;

    //计算 kdj , CRUD
    public Map<String, List<CandleModel>> getKdjBackMap(String symbol, int period, String start, String end, int limit) {
        int granularity = period * 60;
        List<CandleModel> candleList = okexApiUtil.getCandleList(symbol, Integer.toString(granularity), start, end);
        log.info("okexApiUtil.getCandleList size = {}, last = {}", candleList.size(), JSON.toJSONString(candleList.get(0)));
        if (candleList != null && candleList.size() > 20) {
            KDJModel[] kdjModels = KDJ.kdjDefault(candleList, false);
            if (kdjModels != null) {
                return this.calKdjBack(candleList, kdjModels, limit);
            }
        }
        return null;
    }

    private Map<String, List<CandleModel>> calKdjBack(List<CandleModel> candleList, KDJModel[] kdjModels, int limit) {
        double high = 80;
        double low = 20;
        //最新数据
        KDJModel targetKdj = kdjModels[kdjModels.length - 1];
        CandleModel targetCandle = candleList.get(0);

        List<CandleModel> lowList = new ArrayList<>();
        List<CandleModel> highList = new ArrayList<>();
        for (int i = 20; i < limit; i++) {
            //从 历史 遍历 到现在
            KDJModel kdj = kdjModels[i];
            CandleModel candleModel = candleList.get(kdjModels.length - 1 - i);
            //低位
            if (targetKdj.getK() < low && targetKdj.getD() < low) {
                if (targetCandle.getClose() < candleModel.getClose()
                        && targetKdj.getK() > kdj.getK()
                        && targetKdj.getD() > kdj.getD()
                        && targetKdj.getK() > targetKdj.getD()) {
                    lowList.add(candleModel);
                }
            }

            //高位
            if (targetKdj.getK() > high && targetKdj.getD() > high) {
                if (targetCandle.getClose() > candleModel.getClose()
                        && targetKdj.getK() < kdj.getK()
                        && targetKdj.getD() < kdj.getD()
                        && targetKdj.getK() < targetKdj.getD()) {
                    highList.add(candleModel);
                }
            }

        }
        Map<String, List<CandleModel>> map = new HashMap<>();
        map.put(OkexJob.HIGH, highList);
        map.put(OkexJob.LOW, lowList);
        return map;
    }
}
