package top.duwd.fintech.coin.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.fintech.coin.service.OkexKdjCoinService;
import top.duwd.fintech.common.msg.wx.qiye.WxService;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OkexJob {
    @Autowired
    private OkexKdjCoinService okexKdjCoinService;
    @Value("${okex.instrument}")
    private String symbol;

    public static final String LOW = "low";
    public static final String HIGH = "high";

    @Autowired
    private WxService wxService;
    /*
    2019-12-25 21:04:55
    2019-12-25 21:09:55
    2019-12-25 21:14:55
     */
    //15min
    @Scheduled(cron = "55 4/5 * * * ? ")
    public void run15m(){
        int limit = 300;//一天
        Map<String, List<CandleModel>> map = okexKdjCoinService.getKdjBackMap(symbol, 15, null, null, limit);
        List<CandleModel> lowList = map.get(LOW);
        List<CandleModel> highList = map.get(HIGH);

        if (!lowList.isEmpty()){
            wxService.sendText("15 min low 背离");
        }

        if (!highList.isEmpty()){
            wxService.sendText("15 min high 背离");
        }
    }

    /*
    2019-12-25 21:59:00
    2019-12-25 22:59:00
    2019-12-25 23:59:00
     */
    //4h
    @Scheduled(cron = "0 59 * * * ?")
    public void run4h(){
        int limit = 300;//一天
        Map<String, List<CandleModel>> map = okexKdjCoinService.getKdjBackMap(symbol, 240, null, null, limit);
        List<CandleModel> lowList = map.get(LOW);
        List<CandleModel> highList = map.get(HIGH);

        if (!lowList.isEmpty()){
            wxService.sendText("4h low 背离");
        }

        if (!highList.isEmpty()){
            wxService.sendText("4h high 背离");
        }
    }
    /*
    2019-12-26 00:00:00
    2019-12-26 04:00:00
    2019-12-26 08:00:00
     */
    //1d
    @Scheduled(cron = "0 0 0/4 * * ? ")
    public void run1d(){
        int limit = 120;//一天
        Map<String, List<CandleModel>> map = okexKdjCoinService.getKdjBackMap(symbol, 1440, null, null, limit);
        List<CandleModel> lowList = map.get(LOW);
        List<CandleModel> highList = map.get(HIGH);

        if (!lowList.isEmpty()){
            wxService.sendText("1day low 背离");
        }

        if (!highList.isEmpty()){
            wxService.sendText("1day high 背离");
        }
    }
}
