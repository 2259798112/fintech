package top.duwd.fintech.stock.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.common.indicator.KDJ;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.dutil.common.model.KDJModel;
import top.duwd.dutil.date.DateUtil;
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

        List<KdjStockEntity> KdjStockEntityList = new ArrayList<>();

        List<KdjStockEntity> kdjStockEntityListDB = this.findListByCodeAndDate(null, start, end, false);
        HashMap<String, List<KdjStockEntity>> kdjStockMap = new HashMap<>();
        for (KdjStockEntity kdjStockEntity : kdjStockEntityListDB) {
            String stockCode = kdjStockEntity.getStockCode();
            List<KdjStockEntity> kdjStockEntityList = kdjStockMap.get(stockCode);

            if (kdjStockEntityList == null){
                ArrayList<KdjStockEntity> list = new ArrayList<>();
                list.add(kdjStockEntity);
                kdjStockMap.put(stockCode,list);
            }else {
                kdjStockEntityList.add(kdjStockEntity);
            }
        }

        List<StockDailyCandleEntity> dailyCandleEntityListDB = stockDailyService.listDB(null, start, end, false);
        HashMap<String, List<StockDailyCandleEntity>> dailyStockMap = new HashMap<>();
        for (StockDailyCandleEntity dailyCandleEntity : dailyCandleEntityListDB) {
            String stockCode = dailyCandleEntity.getTsCode();
            List<StockDailyCandleEntity> dailyCandleEntityList = dailyStockMap.get(stockCode);

            if (dailyCandleEntityList == null){
                ArrayList<StockDailyCandleEntity> list = new ArrayList<>();
                list.add(dailyCandleEntity);
                dailyStockMap.put(stockCode,list);
            }else {
                dailyCandleEntityList.add(dailyCandleEntity);
            }
        }

        for (StockBasicEntity basicEntity : basicList) {
            String tsCode = basicEntity.getTsCode();
            log.info("stock code {}",tsCode);
            //先检查是否 kdj 有过初始化
            int count = 0;
            if (kdjStockMap.get(tsCode) == null){
                count = 0;
            }else {
                count = kdjStockMap.get(tsCode).size();
            }
            if (count == 0) {//还没有初始化过
                initByCode(basicEntity);
            } else {//已经有过初始化

                //判断是否有未计算的日线数据
                if (kdjStockMap.get(tsCode) == null
                        || dailyStockMap.get(tsCode) == null) {
                    log.error("{} 日线数据异常，为空", JSON.toJSONString(basicEntity));
                    continue;
                } else {
                    //获取最新一天的日线数据
                    StockDailyCandleEntity lastDaily = dailyStockMap.get(tsCode).get(0);
                    //获取最新一天的日线KDJ数据
                    KdjStockEntity lastKdj = kdjStockMap.get(tsCode).get(0);

                    if (lastDaily.getDataTime().equals(lastKdj.getDataTime())) {
                        //日期匹配， 已经有了最新kdj 数据
                        log.info("{} - {} 日期匹配， 已经有了最新kdj 数据", tsCode, basicEntity.getName());
                    } else {
                        List<StockDailyCandleEntity> dailyCandleEntityList = dailyStockMap.get(tsCode);
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
                        kdjStockEntity.setK(kdjModel.getK());
                        kdjStockEntity.setD(kdjModel.getD());
                        kdjStockEntity.setJ(kdjModel.getJ());
                        KdjStockEntityList.add(kdjStockEntity);
                    }
                }
            }
        }
        saveListOfDay(KdjStockEntityList);
    }

    public int save(KdjStockEntity entity) {
        KdjStockEntity dbEntity = this.findOneByCodeAndDate(entity.getStockCode(), entity.getDataTime());
        if (dbEntity == null) {
            Date date = new Date();
            entity.setCreateDate(date);
            entity.setUpdateDate(date);
            entity.setId(null);
            int insert = 0;
            try {
                log.info("kdjStockMapper.insert(entity) {} ", JSON.toJSONString(entity));
                insert = kdjStockMapper.insert(entity);
            } catch (Exception e) {
                log.error("kdjStockMapper.insert(entity) [error] {} ", JSON.toJSONString(entity));
                e.printStackTrace();
            }
            return insert;
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

    public int saveListOfDay(List<KdjStockEntity> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        //判断 最新的数据是否存在
        List<KdjStockEntity> listByDate = this.findListByDate(DateUtil.getStringFromDatePattern(new Date(), DateUtil.PATTERN_yyyyMMdd));

        if (listByDate == null || listByDate.isEmpty()) {

        } else {
            ArrayList<String> strings = new ArrayList<>();
            for (KdjStockEntity entity : listByDate) {
                strings.add(entity.getStockCode());
            }
            List<KdjStockEntity> newList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (strings.contains(list.get(i).getStockCode())) {
                    //已经存在数据
                    log.info("already in db {}",list.get(i).getStockCode());
                } else {
                    newList.add(list.get(i));
                }
            }
        }
        return batchInsert(list);
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
        Example.Criteria criteria = example.createCriteria();
                criteria.andGreaterThanOrEqualTo("dataDate", start)
                .andLessThanOrEqualTo("dataDate", end);

        if (!StringUtils.isEmpty(stockCode)){
            criteria.andEqualTo("stockCode", stockCode);
        }

        if (isAsc) {
            example.orderBy("dataDate").asc();
        } else {
            example.orderBy("dataDate").desc();
        }

        List<KdjStockEntity> list = kdjStockMapper.selectByExample(example);
        return list;
    }

    public List<KdjStockEntity> findListByDate(String day) {
        Example example = new Example(KdjStockEntity.class);
        example.createCriteria().andEqualTo("dataTime", day);
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
        int i = 0;
        try {
            i = kdjStockMapper.insertList(list);
        } catch (Exception e) {
            log.error("batchInsert [error] KdjStockEntity size={}, first={} ", list.size(), JSON.toJSONString(list.get(0)));
            e.printStackTrace();
        }
        return i;
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
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(tsCode)) {
            criteria.andEqualTo("stockCode", tsCode);
        }
        criteria.andLessThanOrEqualTo("k", k)
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

        List<KdjStockEntity> kdjStockEntityListDB = checkKdjStockEntityList(k, d, j, DateUtil.addDay(now, -120), DateUtil.addDay(now, -10), null);
        HashMap<String, List<KdjStockEntity>> kdjStockEntityListMap = new HashMap<>();
        for (KdjStockEntity kdjStockEntity : kdjStockEntityListDB) {
            String stockCode = kdjStockEntity.getStockCode();
            List<KdjStockEntity> kdjStockEntityList = kdjStockEntityListMap.get(stockCode);
            if (kdjStockEntityList==null){
                ArrayList<KdjStockEntity> kdjStockEntities = new ArrayList<>();
                kdjStockEntities.add(kdjStockEntity);
                kdjStockEntityListMap.put(stockCode,kdjStockEntities);
            }else {
                kdjStockEntityList.add(kdjStockEntity);
            }
        }
        //遍历背离
        for (String code : map.keySet()) {

            List<KdjStockEntity> list = kdjStockEntityListMap.get(code);
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
        result.put(LOW, map);
        result.put(BACK, backMap);
        return result;
    }
}
