package top.duwd.fintech.sc.zhihu.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.fintech.common.domain.BookEntity;
import top.duwd.fintech.common.domain.zhihu.dto.BookDto;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuBookEntity;
import top.duwd.fintech.common.mapper.BookMapper;
import top.duwd.fintech.common.mapper.zhihu.ZhihuBookMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZhihuBookService {

    @Autowired
    private ZhihuBookMapper zhihuBookMapper;

    @Autowired
    private BookMapper bookMapper;

    public List<BookDto> setRawBookName(List<BookDto> result) {
        if (result != null && result.size() > 0) {
            List<String> names = new ArrayList<>();
            for (BookDto bookDto : result) {
                if (bookDto.getBookName().contains("\n")) {
                    names.addAll(Arrays.asList(bookDto.getBookName().split("\n")));
                } else {
                    names.add(bookDto.getBookName());
                }
            }

            Example example = new Example(BookEntity.class);
            example.createCriteria().andIn("bookName", names);
            List<BookEntity> list = bookMapper.selectByExample(example);
            if (list != null && list.size() > 0) {
                Map<String, String> map = list.stream().collect(
                        Collectors.toMap(BookEntity::getBookName, BookEntity::getJdLinkUnionLong));

                for (BookDto bookDto : result) {
                    String bookName = bookDto.getBookName();
                    if (bookName.contains("\n")) {
                        String[] temp = bookName.split("\n");
                        for (String name : temp) {
                            if (map.get(name) != null) {
                                bookDto.setBookNameStandard(name);
                                bookDto.setJdUnionLink(map.get(name));
                                break;
                            }
                        }
                    } else {
                        bookDto.setBookNameStandard(bookDto.getBookName());
                        bookDto.setJdUnionLink(map.get(bookDto.getBookNameStandard()));
                    }
                }

            }

            return result;
        }else {
            return result;
        }
    }


    /**
     * 将本书关联至标准书单
     *
     * @param sourceId
     * @param targetBookId
     */
    public int bind(String sourceId, Integer targetBookId) {
        ZhihuBookEntity dbEntity = zhihuBookMapper.selectByPrimaryKey(sourceId);
        if (dbEntity == null) {
            return 0;
        } else {
            dbEntity.setLinkBookId(targetBookId);
            dbEntity.setUpdateTime(new Date());
            dbEntity.setMd(DigestUtils.md5DigestAsHex(JSON.toJSONBytes(dbEntity)));
            return zhihuBookMapper.updateByPrimaryKey(dbEntity);
        }
    }


    /**
     * 将本书关联至标准书单
     *
     * @param sourceId
     */
    public int unbind(String sourceId) {
        ZhihuBookEntity dbEntity = zhihuBookMapper.selectByPrimaryKey(sourceId);
        if (dbEntity == null) {
            return 0;
        } else {
            dbEntity.setLinkBookId(null);
            dbEntity.setUpdateTime(new Date());
            dbEntity.setMd(DigestUtils.md5DigestAsHex(JSON.toJSONBytes(dbEntity)));
            return zhihuBookMapper.updateByPrimaryKey(dbEntity);
        }
    }

    @Transactional
    public int saveOrUpdate(ZhihuBookEntity entity) {
        String id = DigestUtils.md5DigestAsHex((entity.getBookNameAnswer() + entity.getZhihuQuestionId()).getBytes());
        entity.setId(id);
        String md = DigestUtils.md5DigestAsHex(JSON.toJSONBytes(entity));
        entity.setMd(md);

        int count = 0;

        Example example = new Example(ZhihuBookEntity.class);
        example.createCriteria().andEqualTo("id", id);
        ZhihuBookEntity dbEntity = zhihuBookMapper.selectOneByExample(example);
        Date date = new Date();

        if (dbEntity == null) {
            count = save(entity, md, count, date);
        } else {
            count = update(entity, md, count, dbEntity, date);
        }
        return count;
    }

    private int update(ZhihuBookEntity entity, String md, int count, ZhihuBookEntity dbEntity, Date date) {
        if (md.equalsIgnoreCase(dbEntity.getMd())) {
            //不变
            log.info("不变数据");
        } else {
            //修改
            try {
                entity.setCreateTime(dbEntity.getCreateTime());
                entity.setUpdateTime(date);
                count = zhihuBookMapper.updateByPrimaryKey(entity);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("修改异常 {}", JSON.toJSONString(entity));
            }
        }
        return count;
    }

    private int save(ZhihuBookEntity entity, String md, int count, Date date) {
        try {
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
            count = zhihuBookMapper.insert(entity);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存异常 {}", JSON.toJSONString(entity));
        }
        return count;
    }

    public int ignore(String sourceId) {
        ZhihuBookEntity dbEntity = zhihuBookMapper.selectByPrimaryKey(sourceId);
        if (dbEntity == null) {
            return 0;
        } else {
            dbEntity.setValid(0);
            dbEntity.setUpdateTime(new Date());
            dbEntity.setMd(DigestUtils.md5DigestAsHex(JSON.toJSONBytes(dbEntity)));
            return zhihuBookMapper.updateByPrimaryKey(dbEntity);
        }
    }

}
