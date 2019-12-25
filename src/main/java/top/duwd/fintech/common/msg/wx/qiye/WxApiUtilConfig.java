package top.duwd.fintech.common.msg.wx.qiye;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.wx.qiye.WxApiUtil;

@Configuration
public class WxApiUtilConfig {

    @Bean
    public WxApiUtil wxApiUtil(){
        return new WxApiUtil();
    }
}
