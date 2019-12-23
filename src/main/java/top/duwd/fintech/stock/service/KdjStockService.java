package top.duwd.fintech.stock.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.stock.indicator.KDJ;
import top.duwd.dutil.stock.model.CandleModel;
import top.duwd.dutil.stock.model.KDJModel;
import top.duwd.fintech.stock.mapper.KdjStockMapper;
import top.duwd.fintech.stock.model.entity.KdjStockEntity;
import top.duwd.fintech.stock.model.entity.StockBasicEntity;
import top.duwd.fintech.stock.model.entity.StockDailyCandleEntity;

import java.util.*;

@Service
@Slf4j
public class KdjStockService {
    @Autowired
    private KdjStockMapper kdjStockMapper;
    @Autowired
    private StockBasicService stockBasicService;
    @Autowired
    private StockDailyService stockDailyService;

    public static final int MinDailySize = 50;
    public static final int IS_BACK = 1;
    public static final String LOW = "low";
    public static final String BACK = "back";


    /**
     * 计算最新的KDJ 数据
     *
     * @return
     */
    public void dayJob() {
        List<StockBasicEntity> basicList = stockBasicService.listDB();
        if (basicList == null || basicList.isEmpty()) {
            log.error("KdjStockService dayJob List<StockBasicEntity> error ");
        }
        Date now = new Date();
        String end = DateUtil.getStringFromDatePattern(now, DateUtil.PATTERN_yyyyMMdd);
        Date startMinDay = DateUtil.addDay(now, -MinDailySize);
        String start = DateUtil.getStringFromDatePattern(startMinDay, DateUtil.PATTERN_yyyyMMdd);

        for (StockBasicEntity basicEntity : basicList) {
            //先检查是否 kdj 有过初始化
            int count = this.countNow(basicEntity.getTsCode());
            if (count == 0) {//还没有初始化过
                initByCode(basicEntity);
            } else {//已经有过初始化

                //获取 最新的日线信息 （日线任务 应该比此任务 早触发）
                List<StockDailyCandleEntity> dailyCandleEntityList = stockDailyService.listDB(basicEntity.getTsCode(), start, end, false);
                List<KdjStockEntity> kdjStockEntityList = this.findListByCodeAndDate(basicEntity.getTsCode(), start, end, false);

                //判断是否有未计算的日线数据
                if (dailyCandleEntityList == null || dailyCandleEntityList.isEmpty()
                        || kdjStockEntityList == null || kdjStockEntityList.isEmpty()) {
                    log.error("{} 日线数据异常，为空", JSON.toJSONString(basicEntity));
                    continue;
                } else {
                    //获取最新一天的日线数据
                    StockDailyCandleEntity lastDaily = dailyCandleEntityList.get(0);
                    //获取最新一天的日线KDJ数据
                    KdjStockEntity lastKdj = kdjStockEntityList.get(0);

                    if (lastDaily.getDataTime().equals(lastKdj.getDataTime())) {
                        //日期匹配， 已经有了最新kdj 数据
                        log.info("{} - {} 日期匹配， 已经有了最新kdj 数据", basicEntity.getTsCode(), basicEntity.getName());
                    } else {
                        List<CandleModel> candleModels = new ArrayList<>(dailyCandleEntityList.size());
                        for (int i = 0; i < dailyCandleEntityList.size(); i++) {
                            CandleModel candleModel = new CandleModel();
                            BeanUtils.copyProperties(dailyCandleEntityList.get(dailyCandleEntityList.size() - 1 - i), candleModel);
                            candleModels.add(candleModel);
                        }
                        //计算最新的nRSV （时间 从早到晚）
                        Double[] nRSV = KDJ.nRSV(candleModels, 9);

                        KDJModel kdjModel = KDJ.calKdjByKdjAndRSV(lastKdj.getK(), lastKdj.getD(), nRSV[nRSV.length - 1]);
                        KdjStockEntity kdjStockEntity = new KdjStockEntity();
                        BeanUtils.copyProperties(lastDaily, kdjStockEntity);
                        kdjStockEntity.setStockCode(basicEntity.getTsCode());
                        kdjStockEntity.setStockName(basicEntity.getName());
                        kdjStockEntity.setId(null);
                        kdjStockEntityList.add(kdjStockEntity);
                        kdjStockEntity.setK(kdjModel.getK());
                        kdjStockEntity.setD(kdjModel.getD());
                        kdjStockEntity.setJ(kdjModel.getJ());
                        save(kdjStockEntity);
                    }
                }
            }
        }
    }

    public int save(KdjStockEntity entity) {
        KdjStockEntity dbEntity = this.findOneByCodeAndDate(entity.getStockCode(), entity.getDataTime());
        if (dbEntity == null) {
            Date date = new Date();
            entity.setCreateDate(date);
            entity.setUpdateDate(date);
            entity.setId(null);
            return kdjStockMapper.insert(entity);
        } else {
            return 1;
        }
    }

    public int saveList(List<KdjStockEntity> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        //判断 最新的数据是否存在
        KdjStockEntity entity = list.get(list.size() - 1);
        KdjStockEntity dbEntity = this.findOneByCodeAndDate(entity.getStockCode(), entity.getDataTime());

        if (dbEntity == null) {
            return batchInsert(list);
        } else {
            log.info("KdjStockEntity {} {}already in db , pass", entity.getStockName(), entity.getDataTime());
            return 0;
        }
    }

    /**
     * 查询
     *
     * @param stockCode [000001.SZ]
     * @param dateTime  日期[20190101]
     * @return
     */
    public KdjStockEntity findOneByCodeAndDate(String stockCode, String dateTime) {
        Example example = new Example(KdjStockEntity.class);
        example.createCriteria().andEqualTo("stockCode", stockCode).andEqualTo("dataTime", dateTime);
        KdjStockEntity dbEntity = kdjStockMapper.selectOneByExample(example);
        return dbEntity;
    }


    /**
     * 查询list
     *
     * @param stockCode [000001.SZ]
     * @param startDate 开始时间[20190101]
     * @param endDate   结束时间[20190131]
     * @return
     */
    public List<KdjStockEntity> findListByCodeAndDate(String stockCode, String startDate, String endDate, Boolean isAsc) {
        Example example = new Example(KdjStockEntity.class);
        Date start = DateUtil.getDateFromStringPattern(startDate, DateUtil.PATTERN_yyyyMMdd);
        Date end = DateUtil.getDateFromStringPattern(endDate, DateUtil.PATTERN_yyyyMMdd);
        example.createCriteria().andEqualTo("stockCode", stockCode)
                .andGreaterThanOrEqualTo("dataDate", start)
                .andLessThanOrEqualTo("dataDate", end);
        if (isAsc) {
            example.orderBy("dataDate").asc();
        } else {
            example.orderBy("dataDate").desc();
        }
        List<KdjStockEntity> list = kdjStockMapper.selectByExample(example);
        return list;
    }

    /**
     * 根据 code 查询 截止到当前有多少数据
     *
     * @param stockCode
     * @return
     */
    public int countNow(String stockCode) {
        Example example = new Example(KdjStockEntity.class);
        example.createCriteria().andEqualTo("stockCode", stockCode)
                .andLessThanOrEqualTo("dataDate", new Date());
        return kdjStockMapper.selectCountByExample(example);
    }

    public int batchInsert(List<KdjStockEntity> list) {
        log.info("batchInsert KdjStockEntity size={}, first={} ", list.size(), JSON.toJSONString(list.get(0)));
        return kdjStockMapper.insertList(list);
    }

    /**
     * 计算初始 kdj
     *
     * @return
     */
    public void calcKdjInit() {
        //read stock basic
        List<StockBasicEntity> list = stockBasicService.listDB();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (StockBasicEntity basicEntity : list) {
            initByCode(basicEntity);
        }
    }

    private void initByCode(StockBasicEntity basicEntity) {
        //根据 tsCode 获取日线数据
        String tsCode = basicEntity.getTsCode();
        String startDate = basicEntity.getListDate();
        String endDate = DateUtil.getStringFromDatePattern(new Date(), DateUtil.PATTERN_yyyyMMdd);
        //时间 从早到晚
        List<StockDailyCandleEntity> dailyList = stockDailyService.listDB(tsCode, startDate, endDate, true);
        if (dailyList == null || dailyList.isEmpty() || dailyList.size() < MinDailySize) {
            return;
        }

        List<CandleModel> candleModels = new ArrayList<>(dailyList.size());
        List<KdjStockEntity> kdjStockEntityList = new ArrayList<>(dailyList.size());
        for (StockDailyCandleEntity dailyCandleEntity : dailyList) {
            CandleModel candleModel = new CandleModel();
            BeanUtils.copyProperties(dailyCandleEntity, candleModel);
            candleModels.add(candleModel);

            KdjStockEntity kdjStockEntity = new KdjStockEntity();
            BeanUtils.copyProperties(dailyCandleEntity, kdjStockEntity);
            kdjStockEntity.setStockCode(basicEntity.getTsCode());
            kdjStockEntity.setStockName(basicEntity.getName());
            kdjStockEntity.setId(null);
            kdjStockEntityList.add(kdjStockEntity);
        }
        //计算 9天kdj
        KDJModel[] kdjModels = KDJ.kdjDefault(candleModels, true);
        if (kdjModels == null) {
            log.error("kdj calc null");
        }
        for (int i = 0; i < kdjModels.length; i++) {
            KdjStockEntity entity = kdjStockEntityList.get(i);
            entity.setK(kdjModels[i].getK());
            entity.setD(kdjModels[i].getD());
            entity.setJ(kdjModels[i].getJ());
        }
        saveList(kdjStockEntityList);
    }

    /**
     * 获取最近20天的符合条件的数据
     *
     * @param k
     * @param d
     * @param j
     * @return
     */
    public Map<String, KdjStockEntity> check(double k, double d, double j) {
        return this.check(k, d, j, DateUtil.addDay(new Date(), -20), new Date(), null);
    }

    /**
     * 取每个code 的最新一条数据
     *
     * @return
     */
    public Map<String, KdjStockEntity> check(double k, double d, double j, Date start, Date end, String tsCode) {
        List<KdjStockEntity> list = checkKdjStockEntityList(k, d, j, start, end, tsCode);
        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, KdjStockEntity> map = new HashMap<>();
        for (KdjStockEntity kdjStockEntity : list) {
            if (map.get(kdjStockEntity.getStockCode()) == null) {
                map.put(kdjStockEntity.getStockCode(), kdjStockEntity);
            }
        }
        return map;
    }

    /**
     * 获取符合条件的 daily 数据
     *
     * @param tsCode 为空， 遍历所有数据。 不为空 则为指定数据。
     * @return
     */
    private List<KdjStockEntity> checkKdjStockEntityList(double k, double d, double j, Date start, Date end, String tsCode) {
        Example example = new Example(KdjStockEntity.class);
        if (!StringUtils.isEmpty(tsCode)) {
            example.createCriteria().andEqualTo("stockCode", tsCode);
        }
        example.createCriteria().andLessThanOrEqualTo("k", k)
                .andLessThanOrEqualTo("d", d)
                .andLessThanOrEqualTo("j", j)
                .andLessThanOrEqualTo("dataDate", end)
                .andGreaterThanOrEqualTo("dataDate", start);
        example.orderBy("dataDate").desc();

        return kdjStockMapper.selectByExample(example);
    }

    /**
     * 根据过滤后的数据 ， 再次判断背离（前20天 - 前100天）情况
     *
     * @return
     */
    public Map<String, Map<String, KdjStockEntity>> checkKDJBack(double k, double d, double j) {
        Date now = new Date();
        Map<String, KdjStockEntity> map = this.check(k, d, j);

        Map<String, KdjStockEntity> backMap = new HashMap<>();
        //遍历背离
        for (String code : map.keySet()) {
            List<KdjStockEntity> list = checkKdjStockEntityList(k, d, j, DateUtil.addDay(now, -21), DateUtil.addDay(now, -100), code);
            if (list == null || list.isEmpty()) {
                continue;
            }

            KdjStockEntity target = map.get(code);
            ArrayList<KdjStockEntity> backKdjList = new ArrayList<>();
            for (KdjStockEntity kdjStockEntity : list) {
                if (kdjStockEntity.getK() < target.getK()
                        && kdjStockEntity.getD() < target.getD()
                        && kdjStockEntity.getJ() < target.getJ()
                        && kdjStockEntity.getClose() > target.getClose()) {
                    backKdjList.add(kdjStockEntity);
                }
            }

            if (backKdjList.size() > 0) {
                backMap.put(code, target);
            }
        }
        Map<String, Map<String, KdjStockEntity>> result = new HashMap<>();
        result.put(LOW,map);
        result.put(BACK,backMap);
        return result;
    }
}
