package top.duwd.fintech.sc.baidu.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.domain.baidu.dto.BaiduZhihuDto;
import top.duwd.fintech.common.domain.baidu.entity.BaiduZhihuEntity;
import top.duwd.fintech.common.mapper.baidu.BaiduZhihuMapper;

import java.util.*;

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
    }

    public List<BaiduZhihuDto> parse(List<String> keywords, String keywordMain) {
        HashSet<String> keywordsSet = new HashSet<>(keywords);
        ArrayList<BaiduZhihuDto> list = new ArrayList<>();
        HashMap<String, String> pMap = new HashMap<>();
        for (String keyword : keywordsSet) {
            pMap.put("wd", keyword);

            try {
                Thread.sleep(1000 + (long) Math.random() * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                String htmlString = requestBuilder.get(url, hMap, pMap);
                BaiduZhihuDto baiduZhihuDto = parseBaiduHtml(htmlString, "知乎");
                baiduZhihuDto.setKeywords(keyword);
                baiduZhihuDto.setKeywordMain(keywordMain);
                list.add(baiduZhihuDto);

                //保存原始信息
                save(baiduZhihuDto);

            } catch (Exception e) {
                e.printStackTrace();
                log.error("wd={} 百度异常", keyword);
            }
        }

        return list;

    }

    private BaiduZhihuDto parseBaiduHtml(String htmlString, String targetSite) {
        if (StringUtils.isEmpty(htmlString)) {
            return null;
        }

        BaiduZhihuDto baiduZhihuDto = new BaiduZhihuDto();
        HashMap<String, String> map = new HashMap<>();
        //获取连接， 标题，url
        Document document = Jsoup.parse(htmlString);
        Element body = document.body();
        Element content_left = body.getElementById("content_left");
        Elements contents = content_left.getElementsByClass("result");
        for (Element content : contents) {
            Elements src = content.select("span[class=nor-src-wrap]");
            if (src != null && src.size() > 0) {
                if (src.first().text().equalsIgnoreCase(targetSite)) {
                    Elements aTag = content.select("a[data-click]");
                    if (aTag != null && aTag.size() > 0) {
                        String url = aTag.first().attr("href");
                        String title = aTag.first().text();
                        map.put(title, url);
                        System.out.println(title + "=" + url);
                    }
                }
            }

        }
        baiduZhihuDto.setUrls(map);

        return baiduZhihuDto;
    }

    public void parseZhihuLink(List<BaiduZhihuDto> list) {

        for (BaiduZhihuDto baiduZhihuDto : list) {

            //解析 获取真正link
            Map<String, String> linkRawMap = baiduZhihuDto.getUrls();
            Map<String, String> linkRealMap = new HashMap<>();
            Map<String, String> linkRealUpdateMap = new HashMap<>();

            if (linkRawMap != null && linkRawMap.keySet().size() > 0) {

                for (String title : linkRawMap.keySet()) {
                    try {
                        Thread.sleep(1000 + (long) Math.random() * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    String urlRaw = linkRawMap.get(title) + "&wd=";
                    try {
                        String htmlString = requestBuilder.get(urlRaw, hMap, null);
                        Document parse = Jsoup.parse(htmlString);
                        Element noscript = parse.getElementsByTag("noscript").first().child(0);
                        String content = noscript.attr("content");
                        String url = content.split("'")[1];
                        if (StringUtils.endsWithIgnoreCase(url, "updated")) {
                            log.error("有效连接：{}", url);
                            linkRealMap.put(title, url);
                        } else {
                            System.out.println("无效连接：" + url);
                        }
                        baiduZhihuDto.setUrls(linkRealMap);

                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("获取知乎 真是url 异常");
                    }
                }
            }

            if (linkRealMap.keySet().size() > 0) {
                //取到了 updated 类型的url
                for (String title : linkRealMap.keySet()) {
                    String linkRaw = linkRawMap.get(title);
                    String linkRawMd = DigestUtils.md5DigestAsHex(linkRaw.getBytes());
                    BaiduZhihuEntity dbEntity = findByKV("linkRawMd", linkRawMd);
                    if (dbEntity !=null){
                        dbEntity.setLinkReal(linkRealMap.get(title));
                        dbEntity.setUpdateTime(new Date());
                        baiduZhihuMapper.updateByPrimaryKey(dbEntity);
                    }else {
                        log.info("db not in");
                    }
                }
            }



        }

    }

    public List<BaiduZhihuDto> filterNotEmptyList(String keywordMain, List<BaiduZhihuDto> list) {
        ArrayList<BaiduZhihuDto> notEmptyList = new ArrayList<>();
        for (BaiduZhihuDto baiduZhihuDto : list) {
            if (baiduZhihuDto.getUrls().keySet().size() > 0) {
                baiduZhihuDto.setKeywordMain(keywordMain);
                notEmptyList.add(baiduZhihuDto);
            }
        }
        //更新 updated 类型的地址
        return notEmptyList;
    }

    /*
    public static void main(String[] args) {
        RequestBuilder requestBuilder = new RequestBuilder();
        BaiduService baiduService = new BaiduService();
        ArrayList<String> list = new ArrayList<>();
        list.add("降噪耳机");

        List<BaiduZhihuDto> result = baiduService.parse(requestBuilder, list);
        System.out.println(JSON.toJSONString(result));
        for (int i = 0; i < result.size(); i++) {
            baiduService.parseZhihuLink(requestBuilder,result.get(i));
        }
        System.out.println(JSON.toJSONString(result));

    }

     */

    public void save(BaiduZhihuDto baiduZhihuDto) {
        if (baiduZhihuDto != null && baiduZhihuDto.getUrls() != null && baiduZhihuDto.getUrls().keySet().size() > 0) {
            for (String title : baiduZhihuDto.getUrls().keySet()) {
                String linkRaw = baiduZhihuDto.getUrls().get(title);
                String linkRawMd = DigestUtils.md5DigestAsHex(linkRaw.getBytes());

                BaiduZhihuEntity dbEntity = findByKV("linkRawMd", linkRawMd);
                if (dbEntity == null) {
                    BaiduZhihuEntity entity = new BaiduZhihuEntity();
                    entity.setKeywordMain(baiduZhihuDto.getKeywordMain());
                    entity.setKeywords(baiduZhihuDto.getKeywords());
                    entity.setTitle(title);
                    entity.setLinkRaw(linkRaw);
                    entity.setLinkRawMd(linkRawMd);
                    Date date = new Date();
                    entity.setCreateTime(date);
                    entity.setUpdateTime(date);

                    baiduZhihuMapper.insert(entity);
                } else {
                    //老数据
                    log.info("already in db ={}", JSON.toJSONString(dbEntity));
                }
            }
        }
    }


    public BaiduZhihuEntity findByKV(String key, String value) {

        Example example = new Example(BaiduZhihuEntity.class);
        example.createCriteria().andEqualTo(key, value);

        BaiduZhihuEntity dbEntity = baiduZhihuMapper.selectOneByExample(example);
        return dbEntity;
    }
}
