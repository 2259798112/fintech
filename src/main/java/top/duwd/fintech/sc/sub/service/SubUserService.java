package top.duwd.fintech.sc.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.fintech.common.domain.sub.entity.SubQuestionEntity;
import top.duwd.fintech.common.domain.sub.entity.SubUserEntity;
import top.duwd.fintech.common.mapper.sub.SubUserMapper;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SubUserService implements BaseMapper<SubUserEntity>{
    public static final int USER_VALID = 1;
    public static final int USER_INVALID = 0;

    @Autowired
    private SubUserMapper subUserMapper;

    public SubUserEntity findByKV(String k,String v){
        Example example = new Example(SubUserEntity.class);
        example.createCriteria().andEqualTo(k,v);

        SubUserEntity subUserEntity = subUserMapper.selectOneByExample(example);
        return subUserEntity;
    }

    @Override
    public List<SubUserEntity> findListByKV(String k, Object v) {
        Example example = new Example(SubUserEntity.class);
        example.createCriteria().andEqualTo(k,v);

        return subUserMapper.selectByExample(example);
    }

    @Override
    public List<SubUserEntity> findListByMap(Map<String, Object> map) {
        if (map == null || map.keySet().size() == 0) {
            return null;
        }

        Example example = new Example(SubQuestionEntity.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : map.keySet()) {
            criteria.andEqualTo(key, map.get(key));
        }

        return subUserMapper.selectByExample(example);
    }
}
