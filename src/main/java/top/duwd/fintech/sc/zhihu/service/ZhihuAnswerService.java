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
import top.duwd.fintech.common.domain.zhihu.dto.BookDto;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuBookEntity;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuQuestionAnswerPageEntity;
import top.duwd.fintech.common.mapper.zhihu.ZhihuBookMapper;
import top.duwd.fintech.common.mapper.zhihu.ZhihuQuestionAnswerPageMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ZhihuAnswerService {
    @Autowired
    private ZhihuQuestionAnswerPageMapper zhihuQuestionAnswerPageMapper;
    @Autowired
    private ZhihuBookMapper zhihuBookMapper;

    @Transactional
    public int save(ZhihuQuestionAnswerPageEntity entity) {
        log.info("save = {}", JSON.toJSONString(entity));
        ZhihuQuestionAnswerPageEntity db = zhihuQuestionAnswerPageMapper.selectByPrimaryKey(entity.getId());
        if (db == null) {
            return zhihuQuestionAnswerPageMapper.insert(entity);
        } else {
            log.info("answer already in DB");
            return 1;
        }
    }

    public static final String AnswerBase = "https://www.zhihu.com/question/333995687/answer/";
    public static final String SEP = "###";

    public List<BookDto> findBook(Integer qid, int limit) {
        ArrayList<BookDto> arrayList = new ArrayList<>();

        List<ZhihuQuestionAnswerPageEntity> list = getZhihuQuestionAnswerPageEntitiesByQidAndLimit(qid, limit);

        HashMap<String, List<String>> standardBookNameMap = new HashMap<>();//书名号 作者
        extractBookName(list, standardBookNameMap);

        ArrayList<String> bookNameArrayList = new ArrayList<>(standardBookNameMap.keySet());
        Collections.sort(bookNameArrayList);
        HashMap<String, List<String>> notStandardBookNameMap = new HashMap<>();//非书名号 作者
        for (String bookName : bookNameArrayList) {
            for (ZhihuQuestionAnswerPageEntity answer : list) {
                if (answer.getContent().contains(bookName)) {
                    //包含书名，是否包含在之前集合
                    String author = answer.getAuthorImage() + SEP + AnswerBase + answer.getId();//+ SEP + answer.getQuestionId();
                    if (!standardBookNameMap.get(bookName).contains(author)) {
                        bookAnswerAuthor(notStandardBookNameMap, bookName, answer);
                    }
                }
            }
        }

        for (String bookName : bookNameArrayList) {
            BookDto bookDto = new BookDto();
            setField(standardBookNameMap, notStandardBookNameMap, bookName, bookDto);
            if (!StringUtils.isEmpty(bookDto.getBookName().replaceAll("\n", ""))) {
                arrayList.add(bookDto);
            }
        }
        //filter list
        List<BookDto> filterList = filterList(qid, arrayList);

        return filterList;
    }

    private List<ZhihuBookEntity> getZhihuBookEntitiesByQid(Integer qid) {
        Example example = new Example(ZhihuBookEntity.class);
        example.createCriteria().andEqualTo("zhihuQuestionId", qid);
        return zhihuBookMapper.selectByExample(example);
    }

    private void extractBookName(List<ZhihuQuestionAnswerPageEntity> list, HashMap<String, List<String>> standardBookNameMap) {
        for (ZhihuQuestionAnswerPageEntity answer : list) {//遍历获取书名

            List<String> bookNames = ExtractMessage.extractMessage(answer.getContent(), '《', '》');
            if (bookNames.size() > 0) {
                for (String bookName : bookNames) {
                    bookAnswerAuthor(standardBookNameMap, bookName, answer);
                }
            }
        }
    }

    private void setField(HashMap<String, List<String>> standardBookNameMap, HashMap<String, List<String>> mapAnother, String bookName, BookDto bookDto) {
        bookDto.setBookName(bookName);
        ArrayList<String> authorIconUrl = new ArrayList<>();
        ArrayList<String> authorAnswerUrl = new ArrayList<>();
        bookDto.setAuthorIconUrl(authorIconUrl);
        bookDto.setAuthorAnswerUrl(authorAnswerUrl);

        ArrayList<String> authorAnotherIconUrl = new ArrayList<>();
        ArrayList<String> authorAnswerAnotherUrl = new ArrayList<>();
        bookDto.setAuthorAnotherIconUrl(authorAnotherIconUrl);
        bookDto.setAuthorAnswerAnotherUrl(authorAnswerAnotherUrl);

        List<String> imageArrayList = standardBookNameMap.get(bookName);
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
    }

    private List<ZhihuQuestionAnswerPageEntity> getZhihuQuestionAnswerPageEntitiesByQidAndLimit(Integer qid, int limit) {
        Example example = new Example(ZhihuQuestionAnswerPageEntity.class);
        example.createCriteria().andEqualTo("questionId", qid);
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, limit);
        return zhihuQuestionAnswerPageMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    private List<BookDto> filterList(Integer qid, ArrayList<BookDto> arrayList) {
        ArrayList<BookDto> list = new ArrayList<>();
        List<ZhihuBookEntity> dbList = getZhihuBookEntitiesByQid(qid);
        if (dbList == null || dbList.size() == 0) {
            return arrayList;
        }

        HashMap<String, ZhihuBookEntity> hashMap = new HashMap<>();
        for (ZhihuBookEntity entity : dbList) {
            hashMap.put(entity.getId(), entity);
        }

        for (BookDto bookDto : arrayList) {
            String md = DigestUtils.md5DigestAsHex((bookDto.getBookName() + qid).getBytes());
            ZhihuBookEntity dbEntity = hashMap.get(md);
            if (dbEntity != null) {
                bookDto.setLinkBookId(dbEntity.getLinkBookId());
                if (dbEntity.getLinkBookId() != null && dbEntity.getLinkBookId() > 0) {
                    bookDto.setLink(1);
                } else {
                    bookDto.setLink(0);
                }

                if (dbEntity.getValid() == 0) {
                    //remove list
                    continue;
                } else {
                    bookDto.setDb(1);
                    bookDto.setAuthorIconUrl(JSONArray.parseArray(dbEntity.getAuthorIconList()).toJavaList(String.class));
                    bookDto.setAuthorAnswerUrl(JSONArray.parseArray(dbEntity.getAuthorNameList()).toJavaList(String.class));
                    bookDto.setAuthorAnotherIconUrl(JSONArray.parseArray(dbEntity.getAuthorAllIconList()).toJavaList(String.class));
                    bookDto.setAuthorAnswerAnotherUrl(JSONArray.parseArray(dbEntity.getAuthorAllNameList()).toJavaList(String.class));
                    list.add(bookDto);
                }
            } else {
                bookDto.setLinkBookId(0);
                bookDto.setDb(0);
                bookDto.setLink(0);
                list.add(bookDto);
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


    public String findContent(Integer id) {
        if (id != null && id > 0) {
            ZhihuQuestionAnswerPageEntity entity = zhihuQuestionAnswerPageMapper.selectByPrimaryKey(id);
            return entity.getContent();
        } else {
            return "id 异常";
        }
    }
}
