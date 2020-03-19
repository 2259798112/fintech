package top.duwd.fintech.sc.zhihu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.fintech.common.mapper.zhihu.ZhihuAnswerMapper;
import top.duwd.fintech.sc.zhihu.model.entity.ZhihuAnswerEntity;
import top.duwd.fintech.sc.zhihu.model.vo.ZhihuAnswerVo;

@Service
@Slf4j
public class ZhihuAnswerService {
    @Autowired
    private ZhihuAnswerMapper zhihuAnswerMapper;

    public int save(ZhihuAnswerVo vo) {
        String[] split = vo.getAnswerUrl().split("/");
        Integer id = Integer.parseInt(split[split.length -1]);
        Integer qid = Integer.parseInt(split[split.length -3]);

        if (zhihuAnswerMapper.selectByPrimaryKey(id) != null){
            return 1;
        }

        ZhihuAnswerEntity entity = new ZhihuAnswerEntity();
        BeanUtils.copyProperties(vo,entity);
        entity.setId(id);
        entity.setQid(qid);

        return zhihuAnswerMapper.insert(entity);
    }
}
