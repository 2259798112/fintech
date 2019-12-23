package top.duwd.fintech.stock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.stock.model.entity.KDJStockDayEntity;
import top.duwd.fintech.stock.service.KdjStockDayService;
import top.duwd.fintech.stock.service.KdjStockService;
import top.duwd.fintech.stock.service.StockBasicService;
import top.duwd.fintech.stock.service.StockDailyService;

import java.util.List;

@RestController
@RequestMapping("/stock")
@Slf4j
public class StockController {

    @Autowired
    private ApiResultManager apm;
    @Autowired
    private StockBasicService stockBasicService;
    @Autowired
    private StockDailyService stockDailyService;
    @Autowired
    private KdjStockService kdjStockService;
    @Autowired
    private KdjStockDayService kdjStockDayService;

    /*
    @GetMapping(value = "/basic/list/db")
    public ApiResult list() {
        List<StockBasicEntity> list = stockBasicService.listDB();
        return apm.success(list);
    }


    @GetMapping(value = "/basic/list/remote")
    public ApiResult listRemote() {
        List<StockBaseInfo> list = stockBasicService.getStockBasicList();
        stockBasicService.saveStockBasicList(list);
        return apm.success(list);
    }

    @GetMapping(value = "/daily/list/db")
    public ApiResult dailyListDb(@RequestParam String tsCode, @RequestParam String startDate, @RequestParam String endDate, @RequestParam Boolean isAsc) {
//        List<StockDailyCandleEntity> list = stockDailyService.listDB("000001.SZ", "20180101", "20200101");
        List<StockDailyCandleEntity> list = stockDailyService.listDB(tsCode, startDate, endDate,isAsc);
        return apm.success(list);
    }


    @GetMapping(value = "/daily/list/remote")
    public ApiResult dailyListRemote() {
        List<StockCandleModel> list = stockDailyService.listRemote("000001.SZ", "20180101", "20200101");
        stockDailyService.saveList(list);
        return apm.success(list);
    }

    @GetMapping(value = "/daily/init")
    public ApiResult dailyInit(@RequestParam String tsCode,@RequestParam String startDate,@RequestParam String endDate) {
        List<StockBasicEntity> list = stockBasicService.listDB();
        for (StockBasicEntity entity : list) {
            try {
                stockDailyService.listRemote(entity.getTsCode(), "20180101", "20200101");
            } catch (Exception e) {
                log.error("stockDailyService.listRemote");
                e.printStackTrace();
            }
        }
        return apm.success(list);
    }

    @GetMapping(value = "/kdj/init")
    public ApiResult kdj(){
        kdjStockService.calcKdjInit();
        return apm.success();
    }

    @GetMapping(value = "/kdj/check")
    public ApiResult kdjCheck(@RequestParam double k,@RequestParam double d,@RequestParam double j){
        Map<String, Map<String, KdjStockEntity>> map = kdjStockService.checkKDJBack(k, d, j);
        return apm.success(map);
    }

     */
    @GetMapping(value = "/kdj/day/list")
    public ApiResult kdjDayList(String day) {
        List<KDJStockDayEntity> list = kdjStockDayService.list(day);
        return apm.success(list);
    }

    @GetMapping(value = "/kdj/day/list/gen")
    public ApiResult kdjDayList(@RequestParam double k, @RequestParam double d, @RequestParam double j) {
        Object obj = kdjStockDayService.generate(k, d, j);
        return apm.success(obj);
    }
}
