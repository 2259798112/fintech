package top.duwd.fintech.sc.zhihu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuQuestionEntity;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuQuestionTopicEntity;
import top.duwd.fintech.common.mapper.zhihu.ZhihuQuestionMapper;
import top.duwd.fintech.common.mapper.zhihu.ZhihuQuestionTopicMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ZhihuQuestionService {
    @Autowired
    private ZhihuQuestionMapper zhihuQuestionMapper;
    @Autowired
    private ZhihuQuestionTopicMapper zhihuQuestionTopicMapper;
    @Autowired
    private RequestBuilder requestBuilder;

    public static final String QUESTION_BASE = "https://www.zhihu.com/question/";

    @Transactional
    public int parse(int questionId) {
        String url = QUESTION_BASE + questionId;
        int count = 0;
        String htmlString = null;
        try {
            htmlString = requestBuilder.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document html = Jsoup.parse(htmlString);
        Element ele = html.getElementById("js-initialData");

        String text = ele.toString();
        String start = "<script id=\"js-initialData\" type=\"text/json\">";
        String end = "</script>";
        String substring = text.substring(start.length(), text.length() - end.length());
        JSONObject jsonObject = JSON.parseObject(substring);
        count = parseQuestion(count, jsonObject);
        return count;
    }

    public int parseQuestion(int count, JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONObject entities = jsonObject.getJSONObject("initialState").getJSONObject("entities");
            JSONObject questions = entities.getJSONObject("questions");
            Set<String> ids = questions.keySet();
            for (String id : ids) {
                if (zhihuQuestionMapper.selectByPrimaryKey(Integer.parseInt(id)) != null) {
                    count = 1;break;
                }
                JSONObject questionsJSONObject = questions.getJSONObject(id);
                if (questionsJSONObject != null) {
                    ZhihuQuestionEntity entity = questionsJSONObject.toJavaObject(ZhihuQuestionEntity.class);
//                    System.out.println(JSON.toJSONString(entity));
                    JSONObject author = questionsJSONObject.getJSONObject("author");
                    entity.setAuthorId(author.getString("id"));
                    entity.setAuthorName(author.getString("name"));
                    entity.setAuthorUrlToken(author.getString("urlToken"));
                    entity.setAuthorAvatarUrl(author.getString("avatarUrl"));
                    entity.setAuthorIsOrg(author.getBoolean("isOrg"));
                    entity.setAuthorType(author.getString("type"));
                    entity.setAuthorUserType(author.getString("userType"));
                    entity.setAuthorHeadline(author.getString("headline"));
                    entity.setAuthorGender(author.getIntValue("gender"));
                    entity.setAuthorIsAdvertiser(author.getBoolean("isAdvertiser"));
                    entity.setAuthorIsPrivacy(author.getBoolean("isPrivacy"));

                    entity.setCreated(new Date(questionsJSONObject.getLongValue("created") * 1000));
                    entity.setUpdatedTime(new Date(questionsJSONObject.getLongValue("updatedTime") * 1000));

                    count = count + zhihuQuestionMapper.insert(entity);
                    log.info("save = {}", JSON.toJSONString(entity));
                    JSONArray topics = questionsJSONObject.getJSONArray("topics");
                    if (topics != null && topics.size() > 0) {
                        List<ZhihuQuestionTopicEntity> topicList = topics.toJavaList(ZhihuQuestionTopicEntity.class);
                        for (int i = 0; i < topicList.size(); i++) {
                            topicList.get(i).setQid(entity.getId());
                            count = count + zhihuQuestionTopicMapper.insert(topicList.get(i));
                        }
                    }
                    break;
                }
            }
        }
        return count;
    }

}
