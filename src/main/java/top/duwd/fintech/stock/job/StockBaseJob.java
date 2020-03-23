package top.duwd.fintech.stock.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.fintech.stock.service.StockBasicService;

/**
 * 每日更新 新增股票
 */
@Component
@Slf4j
public class StockBaseJob {
    @Autowired
    private StockBasicService stockBasicService;

    //每日6单
    //0 0 18 * * ?
    @Scheduled(cron = "0 10 17 * * ?")
    //@Scheduled(cron = "0 10 16 * * ?")
    public void run18(){
        stockBasicService.getAndSave();
    }
}
