package top.duwd.fintech.coin.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.indicator.KDJ;
import top.duwd.dutil.common.model.CandleModel;

import java.util.List;

@Slf4j
@Service
public class OkexKdjCoinService {
    @Autowired
    private OkexApiUtil okexApiUtil;

    /*
    status -1 底背离
    status  1 顶背离
    {"date":"2020-01-17 11:30:00","value":9095.35,"status":0}
     */
    public JSONObject kdjBack(String symbol, int minutes, int low, int up){
        List<CandleModel> list = okexApiUtil.getCandleList(symbol, String.valueOf(60 * minutes), null, null);
        return KDJ.kdjBack(list, low, up);
    }


}
