package top.duwd.fintech.sc.zhihu.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuBookEntity;
import top.duwd.fintech.common.mapper.zhihu.ZhihuBookMapper;

import java.util.List;

@Slf4j
@Service
public class ZhihuBookService {

    @Autowired
    private ZhihuBookMapper zhihuBookMapper;

    private static Example example = new Example(ZhihuBookEntity.class);


    /**
     * 将本书关联至标准书单
     *
     * @param sourceId
     * @param targetBookId
     */
    public void bind(String sourceId, Integer targetBookId) {

    }


    /**
     * 将本书关联至标准书单
     *
     * @param sourceId
     */
    public void unbind(String sourceId) {

    }

    /**
     * 保存 回答id 提到的书 列表
     *
     * @param list
     */
    public void saveList(List<ZhihuBookEntity> list) {
        //根据 id 获取所有
        for (ZhihuBookEntity entity : list) {
            save(entity);
        }
    }

    @Transactional
    private void save(ZhihuBookEntity entity) {
        String id = DigestUtils.md5DigestAsHex((entity.getBookNameAnswer() + entity.getZhihuQuestionId()).getBytes());
        entity.setId(id);
        String md = DigestUtils.md5DigestAsHex(JSON.toJSONBytes(entity));

        example.createCriteria().andEqualTo("id", id);
        ZhihuBookEntity dbEntity = zhihuBookMapper.selectOneByExample(example);
        if (dbEntity == null) {
            //新增
            try {
                zhihuBookMapper.insert(entity);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("保存异常 {}", JSON.toJSONString(entity));
            }
        } else {
            if (md.equalsIgnoreCase(dbEntity.getMd())) {
                //不变
                log.info("不变数据");
            } else {
                //修改
                try {
                    zhihuBookMapper.updateByPrimaryKey(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("修改异常 {}", JSON.toJSONString(entity));
                }
            }
        }
    }


}
