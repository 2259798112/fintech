package top.duwd.fintech.sc.zhihu;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.fintech.common.mapper.zhihu.ZhihuQuestionMapper;
import top.duwd.fintech.sc.zhihu.model.entity.ZhihuQuestionEntity;
import top.duwd.fintech.sc.zhihu.model.vo.ZhihuQuestionVo;

@Service
@Slf4j
public class ZhihuQuestionService {
    @Autowired
    private ZhihuQuestionMapper zhihuQuestionMapper;

    public int save(ZhihuQuestionVo vo) {
        String[] urls = vo.getUrl().split("/");
        int id = Integer.parseInt(urls[urls.length -1]);
        if (zhihuQuestionMapper.selectByPrimaryKey(id) != null){
            return 1;
        }

        ZhihuQuestionEntity entity = new ZhihuQuestionEntity();
        BeanUtils.copyProperties(vo,entity);
        entity.setId(id);
        entity.setQuestionComment(Integer.parseInt(vo.getQuestionComment()));
        entity.setTopics(JSON.toJSONString(vo.getTopics()));
        entity.setSimilarQuestionsList(JSON.toJSONString(vo.getSimilarQuestionsList()));
        return zhihuQuestionMapper.insert(entity);
    }
}
