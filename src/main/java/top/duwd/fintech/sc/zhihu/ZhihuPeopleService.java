package top.duwd.fintech.sc.zhihu;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.mapper.zhihu.ZhihuPeopleMapper;
import top.duwd.fintech.sc.zhihu.model.entity.ZhihuPeopleEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ZhihuPeopleService {
    @Autowired
    private RequestBuilder requestBuilder;
    @Autowired
    private ZhihuPeopleMapper zhihuPeopleMapper;

    public ZhihuPeopleEntity parse(String url) {
        String page = null;
        try {
            page = requestBuilder.get(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ZhihuPeopleEntity entity = new ZhihuPeopleEntity();
        Document html = Jsoup.parse(page);
        Element body = html.body();
        Elements profileHeaderContents = body.getElementsByClass("ProfileHeader-content");
        String name = profileHeaderContents.first().getElementsByClass("ProfileHeader-name").first().ownText();
        log.info("name={}", name);
        String headline = profileHeaderContents.first().getElementsByClass("ProfileHeader-headline").first().ownText();
        log.info("headline={}", headline);
        String img = body.getElementsByClass("UserAvatar-inner").first().attr("src");
        log.info("img={}", img);

        entity.setName(name);
        entity.setHeadline(headline);
        entity.setUrl(url);
        String[] uids = url.split("/");
        entity.setUid(uids[uids.length - 1]);
        entity.setImg(img);

        ArrayList<String> companyList = new ArrayList<>();
        ArrayList<String> educationList = new ArrayList<>();
        Elements profileHeaderInfos = profileHeaderContents.first().getElementsByClass("ProfileHeader-info");
        for (Element profileHeaderInfo : profileHeaderInfos) {
            Elements com = profileHeaderInfo.getElementsByClass("Icon--company");
            for (Element element : com) {
                List<TextNode> textNodes = element.parent().parent().textNodes();
                for (TextNode textNode : textNodes) {
                    log.info("text node ={}", textNode.text());
                    companyList.add(textNode.text());
                }
            }

            Elements edu = profileHeaderInfo.getElementsByClass("Icon--education");
            for (Element element : edu) {
                List<TextNode> textNodes = element.parent().parent().textNodes();
                for (TextNode textNode : textNodes) {
                    log.info("edu node ={}", textNode.text());
                    educationList.add(textNode.text());
                }
            }
        }

        String company = String.join("/", companyList);
        String education = String.join("/", educationList);
        entity.setCompany(company);
        entity.setEducation(education);

        Element profileMain = body.getElementById("ProfileMain");
        Elements li = profileMain.getElementsByTag("li");
        Integer answers = 0;//回答数
        Integer videos = 0;//视频数
        Integer asks = 0;//提问数
        Integer posts = 0;//文章数
        Integer columns = 0;//专栏
        Integer pins = 0;//想法
        for (Element ele : li) {
            String text = ele.text();
            if (StringUtils.isEmpty(text)) {
                continue;
            }
            text = text.trim().replaceAll(" ", "");
            if (text.startsWith("回答")) {
                answers = Integer.parseInt(text.replaceAll("回答", ""));
                log.info("answers={}", answers);
            }
            if (text.startsWith("视频")) {
                videos = Integer.parseInt(text.replaceAll("视频", ""));
                log.info("videos={}", videos);
            }
            if (text.startsWith("提问")) {
                asks = Integer.parseInt(text.replaceAll("提问", ""));
                log.info("asks={}", asks);
            }
            if (text.startsWith("文章")) {
                posts = Integer.parseInt(text.replaceAll("文章", ""));
                log.info("posts={}", posts);
            }
            if (text.startsWith("专栏")) {
                columns = Integer.parseInt(text.replaceAll("专栏", ""));
                log.info("columns={}", columns);
            }
            if (text.startsWith("想法")) {
                pins = Integer.parseInt(text.replaceAll("想法", ""));
                log.info("pins={}", pins);
            }
        }
        entity.setAnswers(answers);
        entity.setVideos(videos);
        entity.setAsks(asks);
        entity.setPosts(posts);
        entity.setColumns(columns);
        entity.setPins(pins);

        Integer following = 0;//关注人
        Integer followers = 0;//粉丝
        Integer zan = 0;//获赞总数
        Integer like = 0;//喜欢
        Integer fav = 0;//收藏

        Element side = body.getElementsByClass("Profile-sideColumn").first();
        Elements card = side.getElementsByClass("Card");
        String s = card.get(0).text().replaceAll(" ", "")
                .replaceAll("个人成就", "").replaceAll("获得", "")
                .replaceAll(",", "").replaceAll("，", "");

        log.info("s = {}", s);

        String[] zans = s.split("次赞同");
        zan = Integer.parseInt(zans[0]);
        log.info("zan={}", zan);

        String[] likes = zans[1].split("次喜欢");
        like = Integer.parseInt(likes[0]);
        log.info("like={}", like);

        String[] favs = likes[1].split("次收藏");
        fav = Integer.parseInt(favs[0]);
        log.info("fav={}", fav);


        String[] follows = card.get(1).text().replaceAll(" ", "")
                .replaceAll(",", "").replaceAll("关注了", "").split("关注者");
        following = Integer.parseInt(follows[0]);
        log.info("following={}", following);
        followers = Integer.parseInt(follows[1]);
        log.info("followers={}", followers);

        entity.setZan(zan);
        entity.setLike(like);
        entity.setFav(fav);
        entity.setFollowing(following);
        entity.setFollowers(followers);
        return entity;
    }

    public int save(ZhihuPeopleEntity entity) {
        if (entity == null) {
            return 0;
        }


        ZhihuPeopleEntity dbEntity = zhihuPeopleMapper.selectByPrimaryKey(entity.getUid());
        if (dbEntity != null) {
            return 1;
        }
        return zhihuPeopleMapper.insert(entity);
    }
}
