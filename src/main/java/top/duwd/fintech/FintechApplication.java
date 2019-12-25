package top.duwd.fintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;
@SpringBootApplication
@EnableScheduling
@MapperScan({"top.duwd.fintech.stock.mapper","top.duwd.fintech.coin.mapper","top.duwd.fintech.common.mapper"})
@EnableAsync
public class FintechApplication {

    public static void main(String[] args) {
        SpringApplication.run(FintechApplication.class, args);
    }

}
