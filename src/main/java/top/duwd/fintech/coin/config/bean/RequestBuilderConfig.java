package top.duwd.fintech.coin.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duwd.dutil.http.RequestBuilder;

@Configuration
public class RequestBuilderConfig {
    @Bean
    public RequestBuilder requestBuilder(){
        return new RequestBuilder();
    }
}
