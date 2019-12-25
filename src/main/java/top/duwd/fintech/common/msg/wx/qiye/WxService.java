package top.duwd.fintech.common.msg.wx.qiye;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.duwd.dutil.wx.qiye.WxApiUtil;
import top.duwd.fintech.common.domain.ConfigEntity;
import top.duwd.fintech.common.mapper.ConfigMapper;

@Service
@Slf4j
public class WxService {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private WxApiUtil wxApiUtil;

    public static final String k = "wx_feng_actoken";

    public void sendText(String content) {
        String acToken = null;
        try {
            acToken = this.getAcToken();
        } catch (Exception e) {
            log.error("获取 access token 异常");
            return;
        }
        if (acToken != null) {
            String s = wxApiUtil.sendTextToParty(acToken, "1", content, wxApiUtil.AgentId);
            log.info(s);
        }
    }

    public String getAcToken() {
        ConfigEntity configEntity = configMapper.selectByPrimaryKey(k);

        if (configEntity == null) {
            String token = wxApiUtil.defaultAcToken();
            if (token != null) {
                ConfigEntity entity = new ConfigEntity();
                entity.setK(k);
                entity.setV(token);
                configMapper.insert(entity);
                return token;
            }
        } else {
            //判断是否过期
            return configEntity.getV();
        }
        return null;
    }

    //每小时
    @Scheduled(cron = "0 0 * * * ?")
    public void run() {
        configMapper.deleteByPrimaryKey(k);
    }

}
