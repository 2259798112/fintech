package top.duwd.fintech.coin.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.fintech.coin.service.OkexKdjCoinService;
import top.duwd.fintech.common.msg.wx.qiye.WxService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OkexJob {
    @Autowired
    private OkexKdjCoinService okexKdjCoinService;
    private String END = "-USD-200327";
    private String BTC = "BTC" + END;
    private String BCH = "BCH" + END;

    public static final String LOW = "low";
    public static final String HIGH = "high";

    @Autowired
    private WxService wxService;

    /*
        2019-12-26 12:14:50
        2019-12-26 12:29:50
        2019-12-26 12:44:50
        2019-12-26 12:59:50
     */
    //15min
    @Scheduled(cron = "55 4,9,14,19,24,29,34,39,44,49,54,59 * * * ?")
    public void run5m() {
        log.info("5m run start");
        int limit = 300;//一天
        this.run(BTC, "5M", 5, limit);
        this.run(BCH, "5M", 5, limit);
        log.info("5m run end");
    }

    /*
        2019-12-26 12:14:50
        2019-12-26 12:29:50
        2019-12-26 12:44:50
        2019-12-26 12:59:50
     */
    //15min
    @Scheduled(cron = "50 14,29,44,59 * * * ?")
    public void run15m() {
        log.info("15m run start");
        int limit = 300;//一天
        this.run(BTC, "15M", 15, limit);
        this.run(BCH, "15M", 15, limit);
        log.info("15m run end");
    }

    /*
        2019-12-27 03:59:50
        2019-12-27 07:59:50
        2019-12-27 11:59:50
     */
    //4h
    @Scheduled(cron = "50 59 3,7,11,15,19,23 * * ?")
    public void run4h() {
        log.info("4h run start");
        int limit = 300;//一天
        this.run(BTC, "4H", 240, limit);
        this.run(BCH, "4H", 240, limit);
        log.info("4h run start");
    }

    /*
        2019-12-26 23:59:50
        2019-12-27 23:59:50
        2019-12-28 23:59:50
     */
    //1d
    @Scheduled(cron = "50 59 7,19 * * ?")
    public void run1d() {
        log.info("1d run start");
        int limit = 120;//一天
        this.run(BTC, "1Day", 1440, limit);
        this.run(BCH, "1Day", 1440, limit);
        log.info("1d run end");
    }

    public void run(String symbol, String time, int period, int limit) {
        Map<String, List<CandleModel>> map = okexKdjCoinService.getKdjBackMap(symbol, period, null, null, limit);
        List<CandleModel> lowList = map.get(LOW);
        List<CandleModel> highList = map.get(HIGH);

        if (!lowList.isEmpty()) {
            String content = symbol + time + " low Back " + JSON.toJSONString(new Date(), SerializerFeature.WriteDateUseDateFormat);
            log.info(content);
            wxService.sendText(content);
        }

        if (!highList.isEmpty()) {
            String content = symbol + time + " high Back " + JSON.toJSONString(new Date(), SerializerFeature.WriteDateUseDateFormat);
            log.info(content);
            wxService.sendText(content);
        }
    }

}
