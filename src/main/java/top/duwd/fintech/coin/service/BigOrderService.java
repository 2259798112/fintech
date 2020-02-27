package top.duwd.fintech.coin.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.duwd.dutil.coin.binance.BinanceApiUtil;
import top.duwd.dutil.coin.huobi.HuobiApiUtil;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.model.BigOrderModel;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.domain.BigOrderEntity;
import top.duwd.fintech.common.mapper.BigOrderMapper;

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
            return mapper.insert(entity);
        } else {
            return 0;
        }
    }

    public static final Integer MIN_QTY = 100;

    @Async("bigOK")
    @Scheduled(fixedDelay = 150)
    public void okRun(){
        List<BigOrderModel> okList = okexApiUtil.tradeList(requestBuilder,"BTC-USD-200327", 100, MIN_QTY);
        if (okList != null) {
            for (BigOrderModel model : okList) {
                save(model);
            }
        }
    }
    @Async("bigHB")
    @Scheduled(fixedDelay = 150)
    public void hbRun(){
        List<BigOrderModel> hbList = huobiApiUtil.tradeList(requestBuilder,HuobiApiUtil.BTC_CQ, 100, MIN_QTY);
        if (hbList != null) {
            for (BigOrderModel model : hbList) {
                save(model);
            }
        }
    }
//    @Async("bigBN")
//    @Scheduled(fixedDelay = 150)
    public void bnRun(){
        List<BigOrderModel> bnList = binanceApiUtil.tradeList(requestBuilder,"BTCUSDT", 100, MIN_QTY);
        if (bnList != null) {
            for (BigOrderModel model : bnList) {
                save(model);
            }
        }
    }

}
