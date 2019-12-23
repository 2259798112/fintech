package top.duwd.fintech.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.stock.tushare.ApiTushare;

@Configuration
public class TushareApiConfig {

    @Bean
    public ApiTushare apiTushare(){
        ApiTushare apiTushare = new ApiTushare();
        return apiTushare;
    }
}
