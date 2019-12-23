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

    public void run() {
        List<StockBasicEntity> basicEntityList = stockBasicService.listDB();
        Date now = new Date();
        String end = DateUtil.getStringFromDatePattern(now, DateUtil.PATTERN_yyyyMMdd);
        List<StockCandleModel> stockDailyList = stockDailyService.getStockDailyList(null, null, null, end);
        stockDailyService.saveList(stockDailyList);
    }

    //0 0 18 * * ?
    @Scheduled(cron = "0 20 16 * * ?")
    public void run18() {
        run();
    }

}
