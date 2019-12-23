package top.duwd.fintech.stock.model.entity;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "kdj_stock_day")
public class KDJStockDayEntity extends KdjStockEntity {
    private Integer isBack;//1 背离 , 0 低
    private String createDay;// 20191212
}
