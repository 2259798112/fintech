package top.duwd.fintech.sc.baidu.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.date.DateUtil;
import top.duwd.fintech.common.domain.baidu.entity.BaiduCookieEntity;
import top.duwd.fintech.common.domain.baidu.entity.BaiduKeywordEntity;
import top.duwd.fintech.common.mapper.baidu.BaiduCookieMapper;
import top.duwd.fintech.common.mapper.baidu.BaiduKeywordMapper;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BaiduKeywordService {

    @Autowired
    private BaiduKeywordMapper baiduKeywordMapper;
    @Autowired
    private BaiduCookieMapper baiduCookieMapper;

    public int saveList(String main, List<String> list) {
        int count = 0;
        if (list != null && list.size() > 0) {
            for (String keyword : list) {
                count = count + save(main, keyword);
            }
        }

        return count;
    }

    public int save(String main, String keyword) {
        BaiduKeywordEntity dbEntity = findOneByKV("keyword", keyword);
        if (dbEntity == null) {
            BaiduKeywordEntity entity = new BaiduKeywordEntity();

            entity.setMain(main);
            entity.setKeyword(keyword);
            entity.setCreateTime(new Date());
            entity.setUpdateTime(entity.getCreateTime());
            entity.setSearchCount(0);
            return baiduKeywordMapper.insert(entity);
        } else {
            return 0;
        }
    }

    public int updateCount(BaiduKeywordEntity entity) {
        entity.setUpdateTime(new Date());
        entity.setSearchCount(entity.getSearchCount() + 1);
        return baiduKeywordMapper.updateByPrimaryKey(entity);
    }


    public BaiduKeywordEntity findOneByKV(String key, String value) {
        Example example = new Example(BaiduKeywordEntity.class);
        example.createCriteria().andEqualTo(key, value);

        try {
            return baiduKeywordMapper.selectOneByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BaiduKeywordEntity> findList(int limit) {
        Example example = new Example(BaiduKeywordEntity.class);
        example.createCriteria().andEqualTo("searchCount", 0);
        example.setOrderByClause("create_time desc");
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, limit);
        List<BaiduKeywordEntity> list = baiduKeywordMapper.selectByExampleAndRowBounds(example, rowBounds);
        return list;
    }

    public int setCookie(String cookieString) {
        BaiduCookieEntity cookie = new BaiduCookieEntity();
        cookie.setCookie(cookieString);
        cookie.setCreateTime(new Date());
        return baiduCookieMapper.insert(cookie);
    }

    public BaiduCookieEntity findOneCookieRandom() {
        Example example = new Example(BaiduCookieEntity.class);
        example.createCriteria().andBetween("createTime", DateUtil.addDay(new Date(), -3), new Date());

        List<BaiduCookieEntity> list = baiduCookieMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            int index = (int) (Math.random() * 10);
            if (index <= list.size()) {
                return list.get(index);
            } else {
                return list.get(index % list.size());
            }
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {

            System.out.println((int) (Math.random() * 10));
        }
    }
}

