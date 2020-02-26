package top.duwd.fintech.coin.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.coin.binance.BinanceApiUtil;

@Configuration
public class BinanceApiUtilConfig {

    @Bean
    public BinanceApiUtil binanceApiUtil(){
        return new BinanceApiUtil();
    }
}
