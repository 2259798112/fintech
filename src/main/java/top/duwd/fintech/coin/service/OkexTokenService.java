package top.duwd.fintech.coin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.dutil.math.MathUtil;
import top.duwd.fintech.coin.domain.vo.TokenPctVo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OkexTokenService {
    @Autowired
    private OkexApiUtil okexApiUtil;
    public List<TokenPctVo> tokenPct(){
        String json = "{\n" +
                "    \"ABT\":0.1402,\n" +
                "    \"AE\":0.172,\n" +
                "    \"ALGO\":0.252,\n" +
                "    \"BTT\":0.000372,\n" +
                "    \"CMT\":0.0124,\n" +
                "    \"CVC\":0.0205,\n" +
                "    \"EC\":0.00344,\n" +
                "    \"ELF\":0.0601,\n" +
                "    \"HC\":1.534,\n" +
                "    \"HPB\":0.0756,\n" +
                "    \"INT\":0.01693,\n" +
                "    \"ITC\":0.1286,\n" +
                "    \"LRC\":0.025,\n" +
                "    \"PRA\":0.0597,\n" +
                "    \"XLM\":0.0576,\n" +
                "    \"XMR\":65.6,\n" +
                "    \"XRP\":0.2364,\n" +
                "    \"YOU\":0.04096,\n" +
                "    \"ZEN\":10.43,\n" +
                "    \"ZIL\":0.00524\n" +
                "}";
        JSONObject jsonObject = JSON.parseObject(json);
        int week = 60 * 60 * 24 * 7;
        ArrayList<TokenPctVo> vos = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            List<CandleModel> list = okexApiUtil.getTokenCandleModels(key + "-USDT", String.valueOf(week), null, null);
            Double close = list.get(0).getClose();
            Double price = jsonObject.getDouble(key);
            double pct = ((close - price) / price) * 100;
            TokenPctVo vo = new TokenPctVo();
            vo.setToken(key);
            vo.setBuy(price);
            vo.setNow(close);
            vo.setPct(MathUtil.round(pct,2));
            vos.add(vo);
            log.info(JSON.toJSONString(vo));
        }
        List<TokenPctVo> list = vos.stream()
                .sorted(Comparator.comparing(TokenPctVo::getPct).reversed())
                .collect(Collectors.toList());
        return list;
    }
}
