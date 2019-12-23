package top.duwd.fintech.stock.mapper;

import tk.mybatis.mapper.common.Mapper;
import top.duwd.fintech.stock.model.entity.StockDailyCandleEntity;

import java.util.List;

public interface StockDailyCandleMapper extends Mapper<StockDailyCandleEntity> {

    int insertList(List<StockDailyCandleEntity> list);
}
