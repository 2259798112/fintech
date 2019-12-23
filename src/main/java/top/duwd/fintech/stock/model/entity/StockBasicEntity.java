package top.duwd.fintech.stock.model.entity;

import lombok.Data;
import top.duwd.dutil.stock.tushare.model.StockBaseInfo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "stock_basic")
@Data
public class StockBasicEntity extends StockBaseInfo {
    @Id
    private Integer id;

    private Date createDate;
    private Date updateDate;
}
