package top.duwd.fintech.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.http.api.ApiResultManager;

@Configuration
public class ApiResultManagerConfig {

    @Bean
    public ApiResultManager apiResultManager(){
        return new ApiResultManager();
    }
}
