package top.duwd.fintech.stock.mapper;

import tk.mybatis.mapper.common.Mapper;
import top.duwd.fintech.stock.model.entity.KdjStockEntity;

import java.util.List;

public interface KdjStockMapper extends Mapper<KdjStockEntity> {
    int insertList(List<KdjStockEntity> list);
}
