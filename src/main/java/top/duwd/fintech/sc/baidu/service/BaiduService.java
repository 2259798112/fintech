package top.duwd.fintech.sc.baidu.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.domain.BaiduZhihuDto;

import java.util.*;

@Slf4j
@Service
public class BaiduService {

    @Autowired
    private RequestBuilder requestBuilder;

    public static final String url = "https://www.baidu.com/s";
    public static final HashMap<String, String> hMap = new HashMap<>();

    {
        hMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");
    }

    public List<BaiduZhihuDto> parse(List<String> keywords) {
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
                baiduZhihuDto.setKeyword(keyword);
                list.add(baiduZhihuDto);
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

    public void parseZhihuLink(BaiduZhihuDto baiduZhihuDto) {

        //解析 获取真正link
        Map<String, String> urls = baiduZhihuDto.getUrls();
        Map<String, String> updateUrls = new HashMap<String, String>();

        if (urls != null && urls.keySet().size() > 0) {

            for (String title : urls.keySet()) {
                try {
                    Thread.sleep(1000 + (long) Math.random() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                String urlRaw = urls.get(title) + "&wd=";
                try {
                    String htmlString = requestBuilder.get(urlRaw, hMap, null);
                    Document parse = Jsoup.parse(htmlString);
                    Element noscript = parse.getElementsByTag("noscript").first().child(0);
                    String content = noscript.attr("content");
                    String url = content.split("'")[1];
                    if (StringUtils.endsWithIgnoreCase(url, "updated")) {
                        log.error("有效连接：{}",url);
                        updateUrls.put(title, url);
                    } else {
                        System.out.println("无效连接：" + url);
                    }
                    baiduZhihuDto.setUrls(updateUrls);

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("获取知乎 真是url 异常");
                }
            }

        }
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

}
