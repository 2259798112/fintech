package top.duwd.fintech.coin.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.coin.huobi.HuobiApiUtil;

@Configuration
public class HuobiApiUtilConfig {

    @Bean
    public HuobiApiUtil huobiApiUtil(){
        return new HuobiApiUtil();
    }
}
