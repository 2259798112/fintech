package top.duwd.fintech.sc.sub.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.domain.sub.entity.SubQuestionDetailEntity;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuQuestionEntity;
import top.duwd.fintech.common.exception.DuExceptionManager;
import top.duwd.fintech.common.mapper.sub.SubQuestionDetailEntityMapper;
import top.duwd.fintech.common.proxy.ProxyService;

import java.net.Proxy;
import java.util.*;

@Service
@Slf4j
public class SubQuestionDetailService implements BaseMapper<SubQuestionDetailEntity> {

    @Autowired
    private SubQuestionDetailEntityMapper subQuestionDetailEntityMapper;
    @Autowired
    private SubQuestionDetailService subQuestionDetailService;
    @Autowired
    private DuExceptionManager em;
    @Autowired
    private ProxyService proxyService;
    @Autowired
    private RequestBuilder requestBuilder;

    private static final String QUESTION_BASE = "https://www.zhihu.com/question/";

    private static final Map<String, String> hMap = new HashMap<>();

    static {

        hMap.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        hMap.put("accept-encoding", "gzip, deflate, br");
        hMap.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
        hMap.put("cache-control", "no-cache");
        hMap.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
    }


    @Transactional
    public int parse(int questionId) {
        String url = QUESTION_BASE + questionId;
        String htmlString = null;
        //同意使用ip代理

        for (int i = 0; i < 3; i++) {
            //获取1min内的ip订单
            Proxy proxy = proxyService.getProxy(1);
            try {
                htmlString = requestBuilder.getWithProxy(url, hMap, proxy);
                break;//如果成功请求，跳出
            } catch (Exception e) {
                e.printStackTrace();
                log.error("通过proxy={} 获取url={} 异常", JSON.toJSONString(proxy), url);
            }
        }

        if (StringUtils.isEmpty(htmlString)) {
            return 0;
        }

        Document html = Jsoup.parse(htmlString);
        Element ele = html.getElementById("js-initialData");

        String text = ele.toString();
        String start = "<script id=\"js-initialData\" type=\"text/json\">";
        String end = "</script>";
        String substring = text.substring(start.length(), text.length() - end.length());
        JSONObject jsonObject = JSON.parseObject(substring);

        return parseQuestion(jsonObject);
    }



    public int parseQuestion(JSONObject jsonObject) {
        int count = 0;
        if (jsonObject != null) {
            JSONObject entities = jsonObject.getJSONObject("initialState").getJSONObject("entities");
            JSONObject questions = entities.getJSONObject("questions");
            Set<String> ids = questions.keySet();
            for (String id : ids) {
                JSONObject questionsJSONObject = questions.getJSONObject(id);
                if (questionsJSONObject != null) {
                    ZhihuQuestionEntity entity = questionsJSONObject.toJavaObject(ZhihuQuestionEntity.class);

                    JSONObject author = questionsJSONObject.getJSONObject("author");
                    entity.setAuthorId(author.getString("id"));
                    entity.setAuthorName(author.getString("name"));

                    entity.setCreated(new Date(questionsJSONObject.getLongValue("created") * 1000));
                    entity.setUpdatedTime(new Date(questionsJSONObject.getLongValue("updatedTime") * 1000));

                    SubQuestionDetailEntity subQuestionDetailEntity = new SubQuestionDetailEntity();
                    BeanUtils.copyProperties(entities, subQuestionDetailEntity);
                    subQuestionDetailEntity.setQuestionId(entity.getId());

                    try {
                        count += saveSubQuestionDetail(subQuestionDetailEntity);
                        log.info("save success = {}", JSON.toJSONString(subQuestionDetailEntity));
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("save exception = {}", JSON.toJSONString(subQuestionDetailEntity));
                    }
                    break;
                }
            }
        }
        return count;
    }

    @Transactional
    public int saveSubQuestionDetail(SubQuestionDetailEntity subQuestionDetailEntity) {

        Date date = new Date();
        subQuestionDetailEntity.setCreateTime(date);
        subQuestionDetailEntity.setUpdatedTime(date);

        return subQuestionDetailEntityMapper.insert(subQuestionDetailEntity);
    }

    public int saveList(List<SubQuestionDetailEntity> list){

        return 1;
    }


    @Override
    public List<SubQuestionDetailEntity> findListByKV(String k, Object v) {
        Example example = new Example(SubQuestionDetailEntity.class);
        example.createCriteria().andEqualTo(k, v);
        List<SubQuestionDetailEntity> list = subQuestionDetailEntityMapper.selectByExample(example);
        return list;
    }

    @Override
    public List<SubQuestionDetailEntity> findListByMap(Map<String, Object> map) {
        if (map == null || map.keySet().size() == 0) {
            return null;
        }

        Example example = new Example(SubQuestionDetailEntity.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : map.keySet()) {
            criteria.andEqualTo(key, map.get(key));
        }

        List<SubQuestionDetailEntity> list = findListByMap(map);
        return list;
    }
}
