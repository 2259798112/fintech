package top.duwd.fintech.coin.job;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.fintech.coin.domain.entity.ZendeskEntity;
import top.duwd.fintech.coin.service.ZenService;
import top.duwd.fintech.common.msg.wx.qiye.WxService;

import java.util.List;

@Component
@Slf4j
public class ZendeskJob {

    @Autowired
    private WxService wxService;
    @Autowired
    private ZenService zenService;

    //5min
    @Scheduled(cron = "0 */5 * * * ?")
    public void run() {
        log.info("zendesk run start");
        List<ZendeskEntity> list = zenService.check();

        if (list !=null && list.size() > 0){
            wxService.sendText(JSON.toJSONString(list));
        }
        log.info("zendesk run end");
    }

}
