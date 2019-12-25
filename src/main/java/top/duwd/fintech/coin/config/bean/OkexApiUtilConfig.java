package top.duwd.fintech.coin.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.coin.okex.OkexApiUtil;

@Configuration
public class OkexApiUtilConfig {

    @Bean
    public OkexApiUtil okexApiUtil(){
        return new OkexApiUtil();
    }
}
