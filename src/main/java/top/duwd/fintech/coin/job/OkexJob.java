package top.duwd.fintech.coin.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.indicator.I2B;
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
    private OkexApiUtil okexApiUtil;

    @Autowired
    private OkexKdjCoinService okexKdjCoinService;
    private String END = "-USD-200327";
    private String BTC = "BTC" + END;

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
    @Scheduled(cron = "25,55 * * * * ?")
    public void run5m() {
        log.info("5m run start");
        int limit = 100;//一天
        this.run(BTC, "5M", 5, limit);
        log.info("5m run end");
    }

    /*
        2019-12-26 12:14:50
        2019-12-26 12:29:50
        2019-12-26 12:44:50
        2019-12-26 12:59:50
     */
    //15min
    @Scheduled(cron = "25,55 * * * * ?")
    public void run15m() {
        log.info("15m run start");
        int limit = 100;//一天
        this.run(BTC, "15M", 15, limit);
        log.info("15m run end");
    }


    public void run(String symbol, String time, int period, int limit) {
        Map<String, List<CandleModel>> map = okexKdjCoinService.getKdjBackMap(symbol, period, null, null, limit);
        List<CandleModel> lowList = map.get(LOW);
        List<CandleModel> highList = map.get(HIGH);

        if (!lowList.isEmpty()) {
            String content = symbol + " - " + time + " low Back " + JSON.toJSONString(new Date(), SerializerFeature.WriteDateUseDateFormat);
            log.info(content);
            wxService.sendText(content);
        }

        if (!highList.isEmpty()) {
            String content = symbol + " - " + time + " high Back " + JSON.toJSONString(new Date(), SerializerFeature.WriteDateUseDateFormat);
            log.info(content);
            wxService.sendText(content);
        }
    }

    public int check2b(String symbol, int period, int limit) {
        List<CandleModel> candleList = okexApiUtil.getCandleList(symbol, String.valueOf(period * 60), null, null);
        List<CandleModel> list = candleList.subList(0, limit);
        return I2B.b2(list, limit);
    }

    /*
    0 1-5 * * * ?
    2020-01-10 17:01:00
    2020-01-10 17:02:00
    2020-01-10 17:03:00
    2020-01-10 17:04:00
    2020-01-10 17:05:00
    2020-01-10 18:01:00
    2020-01-10 18:02:00
     */
    @Scheduled(cron = "10 * * * * ?")
    public void check1h() {
        String content = BTC + "- 1h 2b -" + getTime();
        log.info(content);
        int b = this.check2b(BTC, 60, 48);
        checkStatus(content, b);
    }

    private void checkStatus(String content, int b) {
        if (b == 1) {
            log.info(content + " [success] high");
            wxService.sendText(content + " [success] high");
        }
        if (b == -1) {
            log.info(content + " [success] low");
            wxService.sendText(content + " [success] low");
        }
    }

    /*
    2020-01-10 18:01:00
    2020-01-10 18:02:00
    2020-01-10 18:03:00
    2020-01-10 18:04:00
    2020-01-10 18:05:00
    2020-01-10 20:01:00
    2020-01-10 20:02:00
     */
    @Scheduled(cron = "20 * * * * ?")
    public void check2h() {
        String content = BTC + "- 2h 2b -" + getTime();
        log.info(content);
        int b = this.check2b(BTC, 60 * 2, 36);
        checkStatus(content, b);
    }

    @Scheduled(cron = "40 * * * * ?")
    public void check4h() {
        String content = BTC + "- 4h 2b -" + getTime();
        log.info(content);

        int b = this.check2b(BTC, 60 * 4, 20);
        checkStatus(content, b);
    }

    public static String getTime() {
        DateTime now = DateTime.now();
        return now.getYear() + "-" + now.getMonthOfYear() + "-" + now.getDayOfYear() + " " + now.getHourOfDay() + "时";
    }

}
