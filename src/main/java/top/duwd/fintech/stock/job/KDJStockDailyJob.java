package top.duwd.fintech.stock.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.dutil.date.DateUtil;
import top.duwd.fintech.stock.model.entity.KDJStockDayEntity;
import top.duwd.fintech.stock.model.entity.KdjStockEntity;
import top.duwd.fintech.stock.service.KdjStockDayService;
import top.duwd.fintech.stock.service.KdjStockService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class KDJStockDailyJob {
    @Autowired
    private KdjStockService kdjStockService;

    @Autowired
    private KdjStockDayService kdjStockDayService;

    //每日18点 获取 当日所有数据
    @Scheduled(cron = "0 30 16 * * ?")
    public void calc(){
        kdjStockService.dayJob();
    }

    //每日18点 计算数据
    @Scheduled(cron = "0 50 16 * * ?")
    public void check(){
        String today = DateUtil.getStringFromDatePattern(new Date(), DateUtil.PATTERN_yyyyMMdd);
        List<KDJStockDayEntity> list = kdjStockDayService.list(today);
        
        if (list !=null && list.size() > 0){
            if (DateUtil.getDayOfWeek() < 6){
                //周末 不发信息
            }
            return;
        }
        Map<String, Map<String, KdjStockEntity>> result = kdjStockService.checkKDJBack(15, 15, 15);
        if (result !=null && result.isEmpty()){
            //存储， 发邮件
            kdjStockDayService.saveAndUpdate(result);
            if (DateUtil.getDayOfWeek() < 6){
                //周末 不发信息
            }
        }
    }

}
