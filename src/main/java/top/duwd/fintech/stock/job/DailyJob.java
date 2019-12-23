package top.duwd.fintech.stock.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.stock.tushare.model.StockCandleModel;
import top.duwd.fintech.stock.model.entity.KdjStockEntity;
import top.duwd.fintech.stock.service.KdjStockDayService;
import top.duwd.fintech.stock.service.KdjStockService;
import top.duwd.fintech.stock.service.StockDailyService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class DailyJob {

    @Autowired
    private StockDailyService stockDailyService;
    @Autowired
    private KdjStockService kdjStockService;
    @Autowired
    private KdjStockDayService kdjStockDayService;

    public String getTodayString(){
        return DateUtil.getStringFromDatePattern(new Date(), DateUtil.PATTERN_yyyyMMdd);
    }

    public void getDayK(){
        List<StockCandleModel> stockDailyList = stockDailyService.getStockDailyList(null, null, null, this.getTodayString());
        stockDailyService.saveList(stockDailyList);
    }

    public void getDayKDJ(){
        //生产kdj day 数据
        kdjStockService.dayJob();

        Map<String, Map<String, KdjStockEntity>> result = kdjStockService.checkKDJBack(10, 10, 10);
        if (result !=null && !result.isEmpty()){
            //存储， 发邮件
            kdjStockDayService.saveAndUpdate(result);
            if (DateUtil.getDayOfWeek() < 6){
                //周末 不发信息
            }
        }
    }

    @Scheduled(cron = "0 30 16 * * ?")
    public void run(){
        getDayK();
        getDayKDJ();
    }


    @Scheduled(cron = "0 0 17 * * ?")
    public void run1(){
        getDayK();
        getDayKDJ();
    }

}
