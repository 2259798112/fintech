package top.duwd.fintech.stock.model.entity;


import lombok.Data;
import top.duwd.dutil.stock.model.BaseCandle;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "kdj_stock")
public class KdjStockEntity extends BaseCandle {
    @Id
    private Integer id;

    private Double k;
    private Double d;
    private Double j;

    private Date dataDate;//` datetime NOT NULL,
    private String dataTime;//` varchar(50) NOT NULL DEFAULT '',
    private String stockName;//` varchar(50) NOT NULL DEFAULT '',
    private String stockCode;//` varchar(50) NOT NULL DEFAULT '',

    private Date createDate;//` datetime NOT NULL,
    private Date updateDate;//` datetime NOT NULL,
}
