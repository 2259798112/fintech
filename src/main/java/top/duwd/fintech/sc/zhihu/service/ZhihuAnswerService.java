package top.duwd.fintech.sc.zhihu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.reg.ExtractMessage;
import top.duwd.fintech.common.domain.zhihu.dto.AnswerDto;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuBookEntity;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuQuestionAnswerPageEntity;
import top.duwd.fintech.common.mapper.zhihu.ZhihuAnswerMapper;
import top.duwd.fintech.common.mapper.zhihu.ZhihuBookMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ZhihuAnswerService {
    @Autowired
    private ZhihuAnswerMapper zhihuAnswerMapper;
    @Autowired
    private ZhihuBookMapper zhihuBookMapper;

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

    public static final String AnswerBase = "https://www.zhihu.com/question/333995687/answer/";
    public static final String SEP = "###";

    public List<AnswerDto> findBook(Integer qid, int limit) {
        ArrayList<AnswerDto> arrayList = new ArrayList<>();

        Example example = new Example(ZhihuQuestionAnswerPageEntity.class);
        example.createCriteria().andEqualTo("questionId", qid);

        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, limit);
        List<ZhihuQuestionAnswerPageEntity> list = zhihuAnswerMapper.selectByExampleAndRowBounds(example, rowBounds);

        HashMap<String, List<String>> map = new HashMap<>();//书名号 作者
        for (ZhihuQuestionAnswerPageEntity answer : list) {//遍历获取书名
            List<String> bookNames = ExtractMessage.extractMessage(answer.getContent(), '《', '》');
            if (bookNames.size() > 0) {
                for (String bookName : bookNames) {
                    bookAnswerAuthor(map, bookName, answer);
                }
            }
        }

        ArrayList<String> bookNameArrayList = new ArrayList<>(map.keySet());
        Collections.sort(bookNameArrayList);
        HashMap<String, List<String>> mapAnother = new HashMap<>();//书名号 作者

        for (String bookName : bookNameArrayList) {
            for (ZhihuQuestionAnswerPageEntity answer : list) {
                if (answer.getContent().contains(bookName)) {
                    //包含书名，是否包含在之前集合
                    String author = answer.getAuthorImage() + SEP + AnswerBase + answer.getId();
                    if (!map.get(bookName).contains(author)) {
                        bookAnswerAuthor(mapAnother, bookName, answer);
                    }
                }
            }
        }

        for (String bookName : bookNameArrayList) {

            AnswerDto answerDto = new AnswerDto();
            answerDto.setBookName(bookName);
            ArrayList<String> authorIconUrl = new ArrayList<>();
            ArrayList<String> authorAnswerUrl = new ArrayList<>();
            answerDto.setAuthorIconUrl(authorIconUrl);
            answerDto.setAuthorAnswerUrl(authorAnswerUrl);

            ArrayList<String> authorAnotherIconUrl = new ArrayList<>();
            ArrayList<String> authorAnswerAnotherUrl = new ArrayList<>();
            answerDto.setAuthorAnotherIconUrl(authorAnotherIconUrl);
            answerDto.setAuthorAnswerAnotherUrl(authorAnswerAnotherUrl);


            List<String> imageArrayList = map.get(bookName);
            for (String imageId : imageArrayList) {
                String[] split = imageId.split(SEP);
                authorIconUrl.add(split[0].replaceAll("pic3", "pic2"));
                authorAnswerUrl.add(split[1]);
            }

            List<String> imageAnotherArrayList = mapAnother.get(bookName);
            if (imageAnotherArrayList != null) {
                for (String imageId : imageAnotherArrayList) {
                    String[] split = imageId.split(SEP);
                    authorAnotherIconUrl.add(split[0].replaceAll("pic3", "pic2"));
                    authorAnswerAnotherUrl.add(split[1]);
                }
            }
            if (StringUtils.isEmpty(answerDto.getBookName().replaceAll("\n",""))){

            }else {
                arrayList.add(answerDto);
            }
        }


        //filter list
        List<AnswerDto> filterList = filterList(qid, arrayList);
        return filterList;
    }

    private List<AnswerDto> filterList(Integer qid, ArrayList<AnswerDto> arrayList) {
        ArrayList<AnswerDto> list = new ArrayList<>();
        Example zhihuBookExample = new Example(ZhihuBookEntity.class);
        zhihuBookExample.createCriteria().andEqualTo("zhihuQuestionId", qid);
        List<ZhihuBookEntity> dbList = zhihuBookMapper.selectByExample(zhihuBookExample);
        if (dbList== null || dbList.size() == 0){
            return arrayList;
        }

        HashMap<String, ZhihuBookEntity> hashMap = new HashMap<>();
        for (ZhihuBookEntity entity : dbList) {
            hashMap.put(entity.getId(),entity);
        }

        for (AnswerDto answerDto : arrayList) {
            String md = DigestUtils.md5DigestAsHex((answerDto.getBookName() + qid).getBytes());
            ZhihuBookEntity dbEntity = hashMap.get(md);
            if (dbEntity != null) {
                if (dbEntity.getLinkBookId() !=null && dbEntity.getLinkBookId() > 0){
                    answerDto.setLink(1);
                }else {
                    answerDto.setLink(0);
                }

                if (dbEntity.getValid() == 0) {
                    //remove list
                    continue;
                } else {
                    answerDto.setDb(1);
                    answerDto.setAuthorIconUrl(JSONArray.parseArray(dbEntity.getAuthorIconList()).toJavaList(String.class));
                    answerDto.setAuthorAnswerUrl(JSONArray.parseArray(dbEntity.getAuthorNameList()).toJavaList(String.class));
                    answerDto.setAuthorAnotherIconUrl(JSONArray.parseArray(dbEntity.getAuthorAllIconList()).toJavaList(String.class));
                    answerDto.setAuthorAnswerAnotherUrl(JSONArray.parseArray(dbEntity.getAuthorAllNameList()).toJavaList(String.class));
                    list.add(answerDto);
                }
            }else {
                answerDto.setDb(0);
                answerDto.setLink(0);
                list.add(answerDto);
            }

        }
        return list;
    }

    private void bookAnswerAuthor(HashMap<String, List<String>> mapAnother, String bookName, ZhihuQuestionAnswerPageEntity answer) {
        List<String> anotherAnswerAuthorImageArrayList = mapAnother.get(bookName);
        if (anotherAnswerAuthorImageArrayList == null) {
            anotherAnswerAuthorImageArrayList = new ArrayList<>();
            mapAnother.put(bookName, anotherAnswerAuthorImageArrayList);
        }

        String author = answer.getAuthorImage() + SEP + AnswerBase + answer.getId();
        if (!anotherAnswerAuthorImageArrayList.contains(author)) {
            anotherAnswerAuthorImageArrayList.add(author);
        }
    }

    public static void main(String[] args) {
        String s = DigestUtils.md5DigestAsHex(("\n\n" + 333995687).getBytes());
        System.out.println(s);
    }

}
