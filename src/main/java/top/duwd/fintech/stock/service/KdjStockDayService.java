package top.duwd.fintech.stock.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.date.DateUtil;
import top.duwd.fintech.stock.mapper.KdjStockDayMapper;
import top.duwd.fintech.stock.model.entity.KDJStockDayEntity;
import top.duwd.fintech.stock.model.entity.KdjStockEntity;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KdjStockDayService {
    @Autowired
    private KdjStockDayMapper kdjStockDayMapper;
    @Autowired
    private KdjStockService kdjStockService;

    public int save(KDJStockDayEntity entity) {
        return kdjStockDayMapper.insert(entity);
    }

    public int update(KDJStockDayEntity entity) {
        return kdjStockDayMapper.updateByPrimaryKey(entity);
    }


    /**
     * 查询指定日期的 kdj 值
     *
     * @return
     */
    public List<KDJStockDayEntity> list(String time) {
        Example example = new Example(KDJStockDayEntity.class);
        example.createCriteria().andEqualTo("createDay", time);
        List<KDJStockDayEntity> list = kdjStockDayMapper.selectByExample(example);
        List<KDJStockDayEntity> sortList = list.stream()
                .sorted(Comparator.comparing(KDJStockDayEntity::getDataDate).reversed())
                .collect(Collectors.toList());
        return sortList;
    }

    public Object generate(double k, double d, double j) {
        String today = DateUtil.getStringFromDatePattern(new Date(), DateUtil.PATTERN_yyyyMMdd);
        List<KDJStockDayEntity> list = list(today);
        if (list != null && list.size() > 0) {
            return list;
        }
        Map<String, Map<String, KdjStockEntity>> result = kdjStockService.checkKDJBack(k, d, j);
        if (result != null && !result.isEmpty()) {
            //存储
            this.saveAndUpdate(result);
        }
        return result;
    }

    public void saveAndUpdate(Map<String, Map<String, KdjStockEntity>> map) {
        if (map == null || map.isEmpty()) {

        } else {
            String createDay = DateUtil.getStringFromDatePattern(new Date(), DateUtil.PATTERN_yyyyMMdd);
            Map<String, KdjStockEntity> lowMap = map.get(KdjStockService.LOW);
            for (String key : lowMap.keySet()) {
                KDJStockDayEntity dayEntity = new KDJStockDayEntity();
                BeanUtils.copyProperties(lowMap.get(key), dayEntity);
                dayEntity.setCreateDay(createDay);
                dayEntity.setId(null);
                KDJStockDayEntity dbEntity = kdjStockDayMapper.selectOne(dayEntity);
                if (dbEntity == null) {
                    dayEntity.setId(null);
                    try {
                        log.info("saveAndUpdate save(dayEntity) ={}", JSON.toJSONString(dayEntity));
                        save(dayEntity);
                    } catch (Exception e) {
                        log.error("saveAndUpdate [error] save(dayEntity) {}", JSON.toJSONString(dayEntity));
                        e.printStackTrace();
                    }
                }
            }

            Map<String, KdjStockEntity> backMap = map.get(KdjStockService.BACK);
            for (String key : backMap.keySet()) {
                KDJStockDayEntity dayEntity = new KDJStockDayEntity();
                BeanUtils.copyProperties(lowMap.get(key), dayEntity);
                dayEntity.setCreateDay(createDay);
                dayEntity.setId(null);
                KDJStockDayEntity dbEntity = kdjStockDayMapper.selectOne(dayEntity);
                if (dbEntity != null) {
                    dbEntity.setIsBack(KdjStockService.IS_BACK);
                    try {
                        log.info("saveAndUpdate update(dbEntity) ={}", JSON.toJSONString(dbEntity));
                        this.update(dbEntity);
                    } catch (Exception e) {
                        log.error("saveAndUpdate [error] update(dbEntity) ={}", JSON.toJSONString(dbEntity));
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
