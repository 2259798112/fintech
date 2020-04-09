package top.duwd.fintech.sc.baidu.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.domain.baidu.entity.BaiduZhihuEntity;
import top.duwd.fintech.common.mapper.baidu.BaiduZhihuMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class BaiduService {

    @Autowired
    private BaiduZhihuMapper baiduZhihuMapper;
    @Autowired
    private RequestBuilder requestBuilder;

    public static final String url = "https://www.baidu.com/s";
    public static final HashMap<String, String> hMap = new HashMap<>();

    {
        hMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");
        hMap.put("Cookie", "BAIDUID=63A84884AF734A69CEBFF64A9599D792:FG=1; BIDUPSID=63A84884AF734A69CEBFF64A9599D792; PSTM=1584946566; BD_UPN=123253; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BDUSS=nc1T01QaHltM3JhN3YyaHRIRmtleDdFWXQzLVFnQ082U2tnU0kzaWR6d0o1YlJlSVFBQUFBJCQAAAAAAAAAAAEAAABRUDliRHV3ZDI1AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAlYjV4JWI1ec; delPer=0; BD_CK_SAM=1; PSINO=3; COOKIE_SESSION=1373_0_9_0_116_110_1_7_3_8_47_24_7435_0_0_0_1586325253_0_1586332200%7C9%230_0_1586332200%7C1; BD_HOME=1; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; H_PS_645EC=4836gg6d9q6tskPA8Z6QakudpkbCsVHLpKt3mHv5o0RTpFfxisSEHsoZP40ueuiACRDQ; sug=3; sugstore=0; ORIGIN=0; bdime=0; H_PS_PSSID=30971_1468_31122_21099_30840_31187_30824_26350_31164_31196; BDSVRTM=18; WWW_ST=1586395510953");
    }


    public BaiduZhihuEntity findByKV(String key, String value) {
        log.info(key + "=" + value);
        Example example = new Example(BaiduZhihuEntity.class);
        example.createCriteria().andEqualTo(key, value);

        try {
            BaiduZhihuEntity dbEntity = baiduZhihuMapper.selectOneByExample(example);
            return dbEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<BaiduZhihuEntity> findListByKV(String key, String value, boolean like) {
        log.info(key + "=" + value);
        Example example = new Example(BaiduZhihuEntity.class);
        if (like) {
            example.createCriteria().andLike(key, "%" + value + "%");
        } else {
            example.createCriteria().andEqualTo(key, value);
        }

        try {
            List<BaiduZhihuEntity> list = baiduZhihuMapper.selectByExample(example);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<BaiduZhihuEntity> searchKeyword(List<String> keywords, String keywordMain) {

        List<BaiduZhihuEntity> list = new ArrayList<>();

        for (String keyword : keywords) {
            List<BaiduZhihuEntity> searchList = searchKeywordFromZhihu(keyword);
            if (searchList != null) {
                list.addAll(searchList);
            }
        }
        return list;
    }

    public List<BaiduZhihuEntity> searchKeywordFromZhihu(String keyword) {

        HashMap<String, String> pMap = new HashMap<>();
        pMap.put("wd", keyword);
        String targetSite = "知乎";

        String htmlString = null;
        try {
            Thread.sleep(1000 + 5000 * (long) Math.random());
            htmlString = requestBuilder.get(url, hMap, pMap);
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("获取百度 {} 搜索页异常", keyword);
            return null;
        }

        Document document = Jsoup.parse(htmlString);
        Element body = document.body();
        if (document.title().contains("安全")) {
            log.error("获取百度 {} 搜索页异常, title={}", keyword, document.title());
        }

        Element content_left = body.getElementById("content_left");
        Elements contents = content_left.getElementsByClass("result");//搜索结果列表
        List<BaiduZhihuEntity> list = new ArrayList<>();
        Date date = new Date();
        for (Element content : contents) {
            BaiduZhihuEntity entity = new BaiduZhihuEntity();

            Elements src = content.select("span[class=nor-src-wrap]");
            if (src != null && src.size() > 0) {
                if (src.first().text().equalsIgnoreCase(targetSite)) {
                    Elements aTag = content.select("a[data-click]");//
                    if (aTag != null && aTag.size() > 0) {
                        //获取原始 百度 zhihu link

                        String url = aTag.first().attr("href");
                        String title = aTag.first().text();
                        entity.setKeywords(keyword);
                        entity.setTitle(title);
                        entity.setLinkRaw(url);
                        entity.setCreateTime(date);
                        entity.setUpdateTime(date);
                        list.add(entity);
                        log.info(title + "=" + url);
                    }
                }
            }
        }
        return list;
    }

    public void parseZhihuLink(List<BaiduZhihuEntity> listRaw) {
        for (BaiduZhihuEntity baiduZhihuEntity : listRaw) {

            String htmlString = null;
            try {
                Thread.sleep(1000 + 5000 * (long) Math.random());
                htmlString = requestBuilder.get(baiduZhihuEntity.getLinkRaw() + "&wd=", hMap, null);
            } catch (Exception e) {
//                e.printStackTrace();
                log.error("解析百度原始连接异常 url={}", baiduZhihuEntity.getLinkRaw());
                continue;
            }

            Document parse = Jsoup.parse(htmlString);
            Element noscript = parse.getElementsByTag("noscript").first().child(0);
            String content = noscript.attr("content");
            String url = content.split("'")[1];

            if (StringUtils.endsWithIgnoreCase(url, "updated")) {
                log.info("有效连接：{}", url);
                baiduZhihuEntity.setLinkRealUpdate(url);
            } else {
                log.info("无效连接：" + url);
                baiduZhihuEntity.setLinkReal(url);
            }
        }
    }

    public List<BaiduZhihuEntity> saveList(String keywordMain, List<BaiduZhihuEntity> listRaw) {
        ArrayList<BaiduZhihuEntity> list = new ArrayList<>();

        for (BaiduZhihuEntity baiduZhihuEntity : listRaw) {
            baiduZhihuEntity.setKeywordMain(keywordMain);

            //查询是否存在，
            //数据库规则， linkReal linkRealUpdate 分别唯一，不能同时存在
            String linkReal = baiduZhihuEntity.getLinkReal();
            String linkRealUpdate = baiduZhihuEntity.getLinkRealUpdate();
            if (StringUtils.isEmpty(linkReal) && StringUtils.isEmpty(linkRealUpdate)) {
                //
                List<BaiduZhihuEntity> titleList = findListByKV("title", baiduZhihuEntity.getTitle(), false);
                if (titleList != null && titleList.size() > 0) {
                    log.info("title={} already in DB", baiduZhihuEntity.getTitle());
                } else {
                    save(baiduZhihuEntity);
                }
            } else if (!StringUtils.isEmpty(linkReal) && StringUtils.isEmpty(linkRealUpdate)) {
                BaiduZhihuEntity linkRealDB = findByKV("linkReal", linkReal);
                if (linkRealDB == null) {
                    save(baiduZhihuEntity);
                }
                list.add(baiduZhihuEntity);
            } else if (StringUtils.isEmpty(linkReal) && !StringUtils.isEmpty(linkRealUpdate)) {
                BaiduZhihuEntity linkRealDB = findByKV("linkRealUpdate", linkRealUpdate);
                if (linkRealDB == null) {
                    save(baiduZhihuEntity);
                }
                list.add(baiduZhihuEntity);
            }
        }
        return list;
    }

    public int save(BaiduZhihuEntity entity) {
        try {
            return baiduZhihuMapper.insert(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<BaiduZhihuEntity> filterList(List<BaiduZhihuEntity> list, Integer answered, Integer updated) {
        ArrayList<BaiduZhihuEntity> arrayList = new ArrayList<>();

        for (BaiduZhihuEntity baiduZhihuEntity : list) {
            if (answered != null) {
                if (baiduZhihuEntity.getAnswered() != null && baiduZhihuEntity.getAnswered().intValue() == answered) {

                } else {
                    continue;
                }
            }

            if (updated != null && updated == 1) {
                if (baiduZhihuEntity.getLinkRealUpdate() != null && baiduZhihuEntity.getLinkRealUpdate().length() > 0) {

                } else {
                    continue;
                }
            }

            arrayList.add(baiduZhihuEntity);
        }

        return arrayList;
    }

    public int updateAnsweredById(Integer id) {
        BaiduZhihuEntity dbEntity = baiduZhihuMapper.selectByPrimaryKey(id);
        if (dbEntity != null) {
            dbEntity.setAnsweredTime(new Date());
            if (dbEntity.getAnswered() != null && dbEntity.getAnswered() == 1) {
                dbEntity.setAnswered(0);
            } else {
                dbEntity.setAnswered(1);
            }
            return baiduZhihuMapper.updateByPrimaryKey(dbEntity);
        } else {
            return 0;
        }
    }
}

