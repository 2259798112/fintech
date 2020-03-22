package top.duwd.fintech.sc.zhihu;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.duwd.fintech.common.mapper.zhihu.ZhihuAnswerMapper;
import top.duwd.fintech.sc.zhihu.model.entity.ZhihuQuestionAnswerPageEntity;

@Service
@Slf4j
public class ZhihuAnswerService {
    @Autowired
    private ZhihuAnswerMapper zhihuAnswerMapper;

    @Transactional
    public int save(ZhihuQuestionAnswerPageEntity entity) {
        log.info("save = {}", JSON.toJSONString(entity));
        ZhihuQuestionAnswerPageEntity db = zhihuAnswerMapper.selectByPrimaryKey(entity.getId());
        if (db == null) {
            return zhihuAnswerMapper.insert(entity);
        } else {
            log.info("answer already in DB");
            return 1;
        }
    }
}
