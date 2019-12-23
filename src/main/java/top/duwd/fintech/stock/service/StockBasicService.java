package top.duwd.fintech.stock.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.stock.tushare.ApiTushare;
import top.duwd.dutil.stock.tushare.model.StockBaseInfo;
import top.duwd.fintech.stock.mapper.StockBasicMapper;
import top.duwd.fintech.stock.model.entity.StockBasicEntity;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StockBasicService {
    @Autowired
    private StockBasicMapper stockBasicMapper;
    @Autowired
    private ApiTushare apiTushare;
    /**
     * 获取 截止当前的所有 市场上的股票 基本信息
     * @return
     */
    public List<StockBaseInfo> getStockBasicList(){
        //20190101
        Date date = new Date();
        String now = DateUtil.getStringFromDatePattern(date, DateUtil.PATTERN_yyyyMMdd);
        log.info("获取 截止当前的所有 市场上的股票 基本信息 ,当前日期 {}",now);
        List<StockBaseInfo> list = null;
        try {
            list = apiTushare.stock_basic(now);
        } catch (IOException e) {
            log.error("获取 截止当前的所有 市场上的股票 基本信息 异常");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 保存 stock base info
     * 重复存在不添加
     * @param list
     * @return
     */
    public int saveStockBasicList(List<StockBaseInfo> list){
        int success = 0;
        for (StockBaseInfo info : list) {
            try {
                int save = this.save(info);
                success = success + save;
            } catch (Exception e) {
                log.error("stock_basic 保存异常 {}", JSON.toJSONString(info));
            }
        }
        return success;
    }


    public int getAndSave(){
        Date date = new Date();
        log.info("getAndSave start {}", DateUtil.getStringFromDatePattern(date,DateUtil.PATTERN_yyyyMMdd_hhmmss));
        List<StockBaseInfo> list = this.getStockBasicList();
        int i = this.saveStockBasicList(list);
        log.info("getAndSave end {} save success {}", DateUtil.getStringFromDatePattern(date,DateUtil.PATTERN_yyyyMMdd_hhmmss),i);
        return i;
    }


    public int save(StockBaseInfo info) {
        //find tsCode 是否存在
        Example example = new Example(StockBasicEntity.class);
        example.createCriteria().andEqualTo("tsCode", info.getTsCode());
        StockBasicEntity dbEntity = stockBasicMapper.selectOneByExample(example);
        if (dbEntity != null) {
            log.info("StockBaseInfo={} already in db ",JSON.toJSONString(info));
            return 1;
        }

        StockBasicEntity entity = new StockBasicEntity();
        BeanUtils.copyProperties(info, entity);
        Date date = new Date();
        entity.setCreateDate(date);
        entity.setUpdateDate(date);
        log.info("stock basic insert {}",JSON.toJSONString(entity));
        int insert = 0;
        try {
            insert = stockBasicMapper.insert(entity);
        } catch (Exception e) {
            log.error("stock basic insert [error] {}",JSON.toJSONString(entity));
            e.printStackTrace();
        }
        return insert;
    }

    /**
     * 从 db 读取 stock basic
     * @return
     */
    public List<StockBasicEntity> listDB() {
        List<StockBasicEntity> list = stockBasicMapper.selectAll();
        return list;
    }
}
