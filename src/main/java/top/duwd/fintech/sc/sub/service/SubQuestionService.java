package top.duwd.fintech.sc.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.duwd.fintech.common.domain.sub.entity.SubQuestionEntity;
import top.duwd.fintech.common.domain.sub.entity.SubUserEntity;
import top.duwd.fintech.common.exception.DuExceptionManager;
import top.duwd.fintech.common.exception.ExceptionDetails;
import top.duwd.fintech.common.mapper.sub.SubQuestionMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubQuestionService implements BaseMapper<SubQuestionEntity> {

    @Autowired
    private SubQuestionMapper subQuestionMapper;

    @Autowired
    private SubUserService subUserService;

    @Autowired
    private DuExceptionManager em;

    public static final int SUB_QUESTION_VALID = 1;
    public static final int SUB_QUESTION_INVALID = 0;
    public static final int SUB_QUESTION_ALL = -1;

    /**
     * 根据user id 获取 所有questionId
     *
     * @param userId
     * @return
     */
    public List<Integer> findQuestionListByUserId(String userId) {
        List<SubQuestionEntity> list = findListByKV("userId", userId);
        if (list != null) {
            Set<Integer> collect = list.stream().map(SubQuestionEntity::getQuestionId).collect(Collectors.toSet());
            return new ArrayList<>(collect);

        } else {
            return null;
        }
    }

    //取消问题订阅
    public int unSubQuestion(String userId, int questionId) {

        //根据 userId，questionId 查询问题
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("questionId", questionId);

        List<SubQuestionEntity> list = findListByMap(map);
        int count = 0;
        if (list == null || list.size() == 0) {
            throw em.create(ExceptionDetails.SUB_QUESTION_NOT_IN_DB);
        } else {

            for (SubQuestionEntity entity : list) {
                if (entity.getValid() == SUB_QUESTION_VALID) {
                    entity.setValid(SUB_QUESTION_INVALID);
                    entity.setUpdateTime(new Date());
                    count = count + subQuestionMapper.updateByPrimaryKey(entity);
                }
            }
        }
        return count;
    }

    //添加 question 订阅 ,入口
    public int addSubQuestionId(String userId, int questionId) {

        //检查用户当前有效的订阅

        //检查用户类型
        SubUserEntity user = subUserService.findByKV("userId", userId);
        int validQuestionCount = 0;
        if (user != null && user.getValid() == SubUserService.USER_VALID) {
            switch (user.getType()) {
                case 0:
                    validQuestionCount = 10;
                case 1:
                    validQuestionCount = 50;
                default:
                    validQuestionCount = 10;
            }
        } else {
            throw em.create(ExceptionDetails.USER_EMPTY);
        }


        List<SubQuestionEntity> validList = this.findQuestionIdByUserId(userId, SUB_QUESTION_VALID);
        if (validList == null) {
            //有效

        } else {
            if (validList.size() < validQuestionCount) {
                //有效

            } else {
                //超过数量，请升级账户，或者取消其他问题ID
                throw em.create(ExceptionDetails.USER_TYPE_LIMIT);
            }
        }

        //添加订阅问题
        return saveSubQuestion(userId, questionId);

    }


    /**
     * 保存用户订阅问题 逻辑
     *
     * @param userId
     * @param questionId
     * @return
     */
    public int saveSubQuestion(String userId, int questionId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("questionId", questionId);

        List<SubQuestionEntity> result = findListByMap(map);
        if (result == null) {
            return insertSubQuestion(userId, questionId);
        } else {
            throw em.create(ExceptionDetails.SUB_QUESTION_DUPLICATE);
        }
    }

    /**
     * 保存订阅问题
     *
     * @param userId
     * @param questionId
     */
    @Transactional
    public int insertSubQuestion(String userId, int questionId) {
        SubQuestionEntity entity = new SubQuestionEntity();
        entity.setValid(SUB_QUESTION_VALID);
        entity.setUserId(userId);
        entity.setQuestionId(questionId);

        Date date = new Date();
        entity.setCreateTime(date);
        entity.setUpdateTime(date);

        return subQuestionMapper.insert(entity);
    }

    //0 无效 ， 1有效，-1 所有
    public List<SubQuestionEntity> findQuestionIdByUserId(String userId, int valid) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("valid", valid);

        List<SubQuestionEntity> list = findListByMap(map);
        return list;
    }

    @Override
    public List<SubQuestionEntity> findListByKV(String k, Object v) {
        Example example = new Example(SubQuestionEntity.class);
        example.createCriteria().andEqualTo(k, v);
        List<SubQuestionEntity> list = subQuestionMapper.selectByExample(example);
        return list;
    }

    @Override
    public List<SubQuestionEntity> findListByMap(Map<String, Object> map) {
        if (map == null || map.keySet().size() == 0) {
            return null;
        }

        Example example = new Example(SubQuestionEntity.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : map.keySet()) {
            criteria.andEqualTo(key, map.get(key));
        }

        List<SubQuestionEntity> list = subQuestionMapper.selectByExample(example);
        return list;
    }

}
