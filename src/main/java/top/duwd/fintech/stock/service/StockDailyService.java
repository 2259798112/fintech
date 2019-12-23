package top.duwd.fintech.stock.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.stock.tushare.ApiTushare;
import top.duwd.dutil.stock.tushare.model.StockCandleModel;
import top.duwd.fintech.stock.mapper.StockDailyCandleMapper;
import top.duwd.fintech.stock.model.entity.StockDailyCandleEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StockDailyService {
    @Autowired
    private StockDailyCandleMapper stockDailyCandleMapper;
    @Autowired
    private ApiTushare apiTushare;

    /**
     * 获取 截止当前的所有 市场上的股票 基本信息
     *
     * @return
     */
    public List<StockCandleModel> getStockDailyList(String tsCode, String startDate, String endDate) {
        //20190101
        Date date = new Date();
        String now = DateUtil.getStringFromDatePattern(date, DateUtil.PATTERN_yyyyMMdd);
        log.info("获取 {} 日线信息 ,当前日期 {}", tsCode, now);
        List<StockCandleModel> list = null;
        try {
            list = apiTushare.daily(tsCode, startDate, endDate);
        } catch (IOException e) {
            log.error("获取 {} 日线信息 异常", tsCode);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 保存 stock base info
     * 重复存在不添加
     *
     * @param list
     * @return
     */
    public int saveList(List<StockCandleModel> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int success = batchInsert(list);
        return success;
    }

    public int batchInsert(List<StockCandleModel> list) {

        ArrayList<StockDailyCandleEntity> arrayList = new ArrayList<>(list.size());
        for (StockCandleModel stockCandleModel : list) {
            StockDailyCandleEntity dbEntity = this.findOneByCodeAndTime(stockCandleModel.getTsCode(), stockCandleModel.getDataTime());
            if (dbEntity != null) {//过滤重复数据
                continue;
            }
            StockDailyCandleEntity entity = new StockDailyCandleEntity();
            BeanUtils.copyProperties(stockCandleModel, entity);
            Date date = new Date();
            entity.setCreateDate(date);
            entity.setUpdateDate(date);
            arrayList.add(entity);
        }
        if (arrayList.size() == 0) {
            log.error("batchInsert arrayList size = 0");
            return 0;
        } else {
            log.info("batchInsert StockDailyCandleEntity {} ", JSON.toJSONString(arrayList));
            return stockDailyCandleMapper.insertList(arrayList);
        }
    }

    /**
     * 根据 code 和 日期 查询日线数据
     *
     * @param code
     * @param time
     * @return
     */
    public StockDailyCandleEntity findOneByCodeAndTime(String code, String time) {
        Example example = new Example(StockDailyCandleEntity.class);
        example.createCriteria().andEqualTo("tsCode", code).andEqualTo("dataTime", time);
        StockDailyCandleEntity entity = stockDailyCandleMapper.selectOneByExample(example);
        return entity;
    }

    /**
     * 获取 db 某股票的日线信息
     *
     * @param tsCode    股票code
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return
     */
    public List<StockDailyCandleEntity> listDB(String tsCode, String startDate, String endDate, Boolean isAsc) {
        Example example = new Example(StockDailyCandleEntity.class);
        Date start = DateUtil.getDateFromStringPattern(startDate, DateUtil.PATTERN_yyyyMMdd);
        Date end = DateUtil.getDateFromStringPattern(endDate, DateUtil.PATTERN_yyyyMMdd);
        example.createCriteria()
                .andEqualTo("tsCode", tsCode)
                .andGreaterThanOrEqualTo("dataDate", start)
                .andLessThanOrEqualTo("dataDate", end);

        if (isAsc) {
            example.orderBy("dataTime").asc();
        } else {
            example.orderBy("dataTime").desc();
        }
        List<StockDailyCandleEntity> list = stockDailyCandleMapper.selectByExample(example);
        return list;
    }

    /**
     * 获取 远程 某股票的日线信息
     *
     * @param tsCode    股票code
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return
     */
    public List<StockCandleModel> listRemote(String tsCode, String startDate, String endDate) {
        List<StockCandleModel> list = this.getStockDailyList(tsCode, startDate, endDate);
        saveList(list);
        return list;
    }


}
