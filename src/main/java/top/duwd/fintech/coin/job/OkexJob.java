package top.duwd.fintech.coin.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.indicator.I2B;
import top.duwd.dutil.common.indicator.KForm;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.fintech.coin.service.OkexKdjCoinService;
import top.duwd.fintech.common.msg.wx.qiye.WxService;

import java.util.List;

//@Component
@Slf4j
public class OkexJob {
    @Autowired
    private OkexApiUtil okexApiUtil;

    @Autowired
    private OkexKdjCoinService okexKdjCoinService;
    private String END = "-USD-200327";
    private String BTC = "BTC" + END;
    private String LTC = "LTC" + END;
    private String BCH = "BCH" + END;

    public static final String LOW = "low";
    public static final String HIGH = "high";

    @Autowired
    private WxService wxService;

    //15min
    @Scheduled(cron = "50 14,29,44,59 * * * ?")
    public void run15m() {
        log.info("15m run start");
        run(BTC,15,20,80);
        log.info("15m run end");
    }


    public void run(String symbol, int minutes, int low, int up) {
        JSONObject jsonObject = okexKdjCoinService.kdjBack(symbol, minutes, low, up);
        String content = JSON.toJSONString(jsonObject);
        Boolean isSend = false;
        if (jsonObject.getInteger("status") == 1){
            log.info("high kdj back");
            isSend = true;
        }
        if (jsonObject.getInteger("status") == -1){
            log.info("low kdj back");
            isSend = true;
        }
        if (isSend){
            wxService.sendText(content);
        }
        log.info(content);
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

    @Scheduled(cron = "0 58 3,7,11,15,19,23 * * ?")
    public void check4hTT() {
        checkTT(BTC, 60 * 4);
        checkTT(LTC, 60 * 4);
    }


    @Scheduled(cron = "0 58 7 * * ?")
    public void check1dTT() {
        checkTT(BTC, 60 * 24);
        checkTT(LTC, 60 * 24);
    }


    private void checkTT(String symbol, int minutes) {
        List<CandleModel> candleList = okexApiUtil.getCandleList(symbol, String.valueOf(minutes * 60), null, null);
        if (candleList != null && candleList.size() > 0) {
            if (KForm.TT(candleList.get(0), false, 0.01)) {
                wxService.sendText(symbol + " " + minutes + " 锤子 " + JSON.toJSONString(candleList.get(0).getDataDate(), SerializerFeature.WriteDateUseDateFormat));
            }
            if (KForm.TT(candleList.get(0), true, 0.01)) {
                wxService.sendText(symbol + " " + minutes + " 倒锤子 " + JSON.toJSONString(candleList.get(0).getDataDate(), SerializerFeature.WriteDateUseDateFormat));
            }
        }
    }
}
