package top.duwd.fintech.coin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.duwd.dutil.coin.okex.OkexApiUtil;
import top.duwd.dutil.common.indicator.KDJ;
import top.duwd.dutil.common.model.CandleModel;
import top.duwd.dutil.common.model.KDJModel;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/okex")
@RestController
public class OkexController {
    @Autowired
    private OkexApiUtil okexApiUtil;
    @Autowired
    private ApiResultManager apm;


    @GetMapping("/kdj/contact/{symbol}/{period}/{high}/{low}/{diff}")
    public ApiResult kdjFilter(@PathVariable String symbol, @PathVariable Integer period
            , @PathVariable Integer diff
            , @PathVariable Integer high
            , @PathVariable Integer low) {

        List<CandleModel> candleList = okexApiUtil.getCandleList(symbol, Integer.toString(period * 60), null, null);
        KDJModel[] kdjModels = KDJ.kdjDefault(candleList, false);
        ArrayList<CandleModel> lowList = new ArrayList<CandleModel>();
        ArrayList<CandleModel> highList = new ArrayList<CandleModel>();

        for (int i = 0; i < kdjModels.length; i++) {
            CandleModel candleModel = new CandleModel();
            int reIndex = kdjModels.length - 1 - i;
            BeanUtils.copyProperties(candleList.get(reIndex), candleModel);
            KDJModel kdjModel = kdjModels[i];
            Date date = candleModel.getDataDate();
            String time = JSON.toJSONString(date, SerializerFeature.WriteDateUseDateFormat);
            String timeKDJ = time + " k=" + kdjModel.getK() + " d=" + kdjModel.getD() + " j=" + kdjModel.getJ();
            candleModel.setDataTime(timeKDJ);

            if (kdjModel.getD() < low && kdjModel.getK() < low && Math.abs(kdjModel.getK() - kdjModel.getD()) < diff) {
                lowList.add(candleModel);
            }

            if (kdjModel.getD() > high && kdjModel.getK() > high && Math.abs(kdjModel.getK() - kdjModel.getD()) < diff) {
                highList.add(candleModel);
            }
        }

        HashMap<String, List<CandleModel>> map = new HashMap<>();
        map.put("low", lowList);
        map.put("high", highList);

        return apm.success(map);
    }

    @GetMapping(value = "/hl")
    public ApiResult hl(@RequestParam(value = "symbol") String symbol,
                        @RequestParam(value = "period") Integer period,
                        @RequestParam(value = "hl") Double hl) {


        List<CandleModel> candleList = okexApiUtil.getCandleList(symbol, Integer.toString(period * 60), null, null);


        KDJModel[] kdjModels = KDJ.kdjDefault(candleList, false);

        ArrayList<CandleModel> list = new ArrayList<>();
        int size = candleList.size();
        for (int i = 0; i < size; i++) {
            Double lastPrice = candleList.get(i + 1).getClose();

            CandleModel candleModel = candleList.get(i);
            double v = candleModel.getHigh() - candleModel.getLow();
            if (i == size - 1 - 1) {
                break;
            } else {
                if ((v / lastPrice) > (hl / 100)) {

                    Temp temp = new Temp();
                    BeanUtils.copyProperties(candleModel, temp);
                    temp.setK(kdjModels[size - 1 - i].getK());
                    temp.setD(kdjModels[size - 1 - i].getD());
                    temp.setJ(kdjModels[size - 1 - i].getJ());
                    temp.setHl(v / lastPrice);
                    list.add(temp);
                }
            }
        }
        return apm.success(list);
    }

    class Temp extends CandleModel {
        private Double k;
        private Double d;
        private Double j;
        private Double hl;

        public Double getHl() {
            return hl;
        }

        public void setHl(Double hl) {
            this.hl = hl;
        }

        public Double getK() {
            return k;
        }

        public void setK(Double k) {
            this.k = k;
        }

        public Double getD() {
            return d;
        }

        public void setD(Double d) {
            this.d = d;
        }

        public Double getJ() {
            return j;
        }

        public void setJ(Double j) {
            this.j = j;
        }
    }

}
