package top.duwd.fintech.stock.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.stock.tushare.model.StockCandleModel;
import top.duwd.fintech.stock.model.entity.StockBasicEntity;
import top.duwd.fintech.stock.service.StockBasicService;
import top.duwd.fintech.stock.service.StockDailyService;

import java.util.Date;
import java.util.List;

@Component
public class StockDailyJob {

    @Autowired
    private StockBasicService stockBasicService;
    @Autowired
    private StockDailyService stockDailyService;

    public void run(){
        List<StockBasicEntity> basicEntityList = stockBasicService.listDB();
        for (StockBasicEntity basicEntity : basicEntityList) {
            Date now = new Date();
            Date start15 = DateUtil.addDay(now, -15);
            String end = DateUtil.getStringFromDatePattern(now, DateUtil.PATTERN_yyyyMMdd);
            String start = DateUtil.getStringFromDatePattern(start15, DateUtil.PATTERN_yyyyMMdd);
            List<StockCandleModel> stockDailyList = stockDailyService.getStockDailyList(basicEntity.getTsCode(), start, end);
            if (stockDailyList == null || stockDailyList.isEmpty()){
                continue;
            }
            stockDailyService.saveList(stockDailyList);
        }
    }

    //0 0 18 * * ?
    @Scheduled(cron = "0 30 17 * * ?")
    public void run18(){
        run();
    }

}
