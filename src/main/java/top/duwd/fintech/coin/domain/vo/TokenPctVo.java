package top.duwd.fintech.coin.domain.vo;

import lombok.Data;

@Data
public class TokenPctVo {
    private String token;
    private double buy;
    private double now;
    private double pct;
}
