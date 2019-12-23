package top.duwd.fintech.stock.model.entity;

import lombok.Data;
import top.duwd.dutil.stock.tushare.model.StockCandleModel;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "stock_daily")
@Data
public class StockDailyCandleEntity extends StockCandleModel {
    @Id
    private Integer id;

    private Date createDate;
    private Date updateDate;

}
