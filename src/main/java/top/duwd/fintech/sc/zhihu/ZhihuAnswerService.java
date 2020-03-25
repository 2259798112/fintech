package top.duwd.fintech.sc.zhihu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.reg.ExtractMessage;
import top.duwd.fintech.common.mapper.zhihu.ZhihuAnswerMapper;
import top.duwd.fintech.sc.zhihu.model.entity.ZhihuQuestionAnswerPageEntity;

import java.util.*;

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

    public List<JSONObject> filter(Integer qid) {
        ArrayList<JSONObject> arrayList = new ArrayList<>();

        Example example = new Example(ZhihuQuestionAnswerPageEntity.class);
        example.createCriteria().andEqualTo("questionId", qid);
        List<ZhihuQuestionAnswerPageEntity> list = zhihuAnswerMapper.selectByExample(example);

        Map<String, HashSet<String>> map = new HashMap<>();
        Map<String, HashSet<String>> mapWithoutMark = new HashMap<>();

        //筛选《》内部的名字
        HashSet<String> bookNames = new HashSet<>();
        long s1 = System.currentTimeMillis();

        for (ZhihuQuestionAnswerPageEntity entity : list) {
            List<String> books = ExtractMessage.extractMessage(entity.getContent(), '《', '》');
            for (String book : books) {
                bookNames.add(book);
                Set<String> urls = map.get(book);
                if (urls == null) {
                    HashSet<String> set = new HashSet<>();
                    set.add(entity.getAuthorImage());
                    map.put(book, set);
                } else {
                    urls.add(entity.getAuthorImage());
                }
            }
        }

        ArrayList<String> bookNamesArrayList = new ArrayList<>(bookNames);
        Collections.sort(bookNamesArrayList);
        long e1 = System.currentTimeMillis();
        log.info("1 = {}ms",e1-s1);


        long s2 = System.currentTimeMillis();
        //根据 bookName 去掉《》 查询作者
        for (String bookName : bookNamesArrayList) {
            HashSet<String> names = map.get(bookName);
            HashSet<String> namesWithoutMark = mapWithoutMark.get(bookName);

            for (ZhihuQuestionAnswerPageEntity entity : list) {
                int i = entity.getContent().indexOf(bookName);
                if (i >= 0) {
                    if (names.contains(entity.getAuthorImage())){
                        //重复 回答者
                    }else {
                        //新发现的 回答者

                        if (namesWithoutMark == null) {
                            namesWithoutMark = new HashSet<>();
                            mapWithoutMark.put(bookName, namesWithoutMark);
                        }
                        namesWithoutMark.add(entity.getAuthorImage());
                    }
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bookName",bookName);
            jsonObject.put("author",map.get(bookName));
            jsonObject.put("authorWithoutMark",mapWithoutMark.get(bookName));
            arrayList.add(jsonObject);
        }
        long e2 = System.currentTimeMillis();
        log.info("2 = {}ms",e2-s2);

        log.info(JSON.toJSONString(arrayList));
        return arrayList;
    }

}
