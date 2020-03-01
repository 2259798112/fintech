package top.duwd.fintech.coin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.coin.binance.BinanceApiUtil;
import top.duwd.dutil.coin.huobi.HuobiApiUtil;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.model.BigOrderModel;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.dutil.math.MathUtil;
import top.duwd.fintech.common.domain.BigOrderEntity;
import top.duwd.fintech.common.mapper.BigOrderMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class BigOrderService {

    @Autowired
    private BigOrderMapper mapper;
    @Autowired
    private OkexApiUtil okexApiUtil;
    @Autowired
    private HuobiApiUtil huobiApiUtil;
    @Autowired
    private BinanceApiUtil binanceApiUtil;
    @Autowired
    private RequestBuilder requestBuilder;

    public int save(BigOrderModel model) {
        BigOrderEntity entity = new BigOrderEntity();
        BeanUtils.copyProperties(model, entity);

        if (mapper.selectByPrimaryKey(entity.getId()) == null) {
            entity.setAmount(entity.getSize() * 100 / entity.getPrice());
            log.info(JSON.toJSONString(entity));
            try {
                mapper.insert(entity);
            } catch (DuplicateKeyException e) {
                log.error("DuplicateKeyException");
            } catch (Exception e) {
                log.error("unknown Exception");
            }
            return 1;
        } else {
            return 0;
        }
    }

    public static final Integer MIN_QTY = 100;
    public static final ArrayList<String> ids = new ArrayList<>();

//    @Async("bigOK")
//    @Scheduled(fixedDelay = 150)
    public void okRun() {
        List<BigOrderModel> okList = okexApiUtil.tradeList(requestBuilder, "BTC-USD-200626", 100, MIN_QTY);
        if (okList != null) {
            for (BigOrderModel model : okList) {
                if (!ids.contains(model.getId())) {
                    log.info("ids size " + ids.size());
                    ids.add(model.getId());
                    save(model);
                }
            }
        }
    }

//    @Async("bigHB")
//    @Scheduled(fixedDelay = 150)
    public void hbRun() {
        List<BigOrderModel> hbList = huobiApiUtil.tradeList(requestBuilder, HuobiApiUtil.BTC_CQ, 100, MIN_QTY);
        if (hbList != null) {
            for (BigOrderModel model : hbList) {
                if (!ids.contains(model.getId())) {
                    log.info("ids size " + ids.size());
                    ids.add(model.getId());
                    save(model);
                }
            }
        }
    }

    //15min
    @Scheduled(cron = "0 */2 * * * ?")
    public void refresh() {
        if (ids.size() > 500) {
            int limit = ids.size() - 500;
            for (int i = 0; i < limit; i++) {
                ids.remove(0);
            }
        }
    }

    //根据 开始 结束 时间，平台 计算 多空
    //minQty 是 BTC 数量
    public JSONObject cal(Date start, Date end, String plat, int minQty) {
        Example example = new Example(BigOrderEntity.class);
        example.createCriteria()
                .andGreaterThanOrEqualTo("amount", minQty)
                .andGreaterThan("ts", start)
                .andLessThanOrEqualTo("ts", end);

        List<BigOrderEntity> list = mapper.selectByExample(example);
        JSONObject jsonObject = new JSONObject();
        String p = plat.toLowerCase();
        String[] ps = {"huobi", "okex"};

        if (list != null) {
            double buy = 0;
            double sell = 0;
            for (BigOrderEntity entity : list) {
                if (Arrays.asList(ps).contains(p)) { //Huobi OKEX
                    if (p.equalsIgnoreCase(entity.getPlat())) {

                        if ("sell".equalsIgnoreCase(entity.getSide())) {//主动卖单
                            sell += entity.getAmount();
                        } else {
                            buy += entity.getAmount();
                        }

                    }

                } else {//全平台
                    if ("sell".equalsIgnoreCase(entity.getSide())) {//主动卖单
                        sell += entity.getAmount();
                    } else {
                        buy += entity.getAmount();
                    }
                }
            }
            jsonObject.put("plat", plat);
            jsonObject.put("sell", MathUtil.round(sell, 2));
            jsonObject.put("buy", MathUtil.round(buy, 2));
        }
        return jsonObject;
    }


    /**
     * 获取记录列表，以时间倒序，最多200
     * @param start
     * @param end
     * @param plat
     * @param min
     * @return
     */
    public List<BigOrderEntity> list(Date start, Date end, String plat, Integer min) {
        Example example = new Example(BigOrderEntity.class);
        example.createCriteria()
                .andEqualTo("plat",plat)
                .andGreaterThanOrEqualTo("amount", min)
                .andGreaterThan("ts", start)
                .andLessThanOrEqualTo("ts", end);

        example.orderBy("ts").desc();

        List<BigOrderEntity> list = mapper.selectByExampleAndRowBounds(example,new RowBounds(RowBounds.NO_ROW_OFFSET,200));

        return list;
    }
}
