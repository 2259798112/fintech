package top.duwd.fintech.sc.baidu.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.fintech.common.domain.baidu.entity.BaiduCookieEntity;
import top.duwd.fintech.common.domain.baidu.entity.BaiduKeywordEntity;
import top.duwd.fintech.common.domain.baidu.entity.BaiduZhihuEntity;
import top.duwd.fintech.sc.baidu.service.BaiduKeywordService;
import top.duwd.fintech.sc.baidu.service.BaiduService;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class BaiduZhihuJob {

    @Autowired
    private BaiduKeywordService baiduKeywordService;
    @Autowired
    private BaiduService baiduService;

    @Scheduled(fixedRate = 1000 * 60 )//10minutes
    public void run() {
        log.info("baidu job 关键词查询");
        List<BaiduKeywordEntity> list = baiduKeywordService.findList(10);
        if (list != null && list.size() > 0) {
            BaiduCookieEntity cookie = baiduKeywordService.findOneCookieRandom();
            if (cookie != null){
                BaiduService.hMap.put("Cookie",cookie.getCookie());
            }else {
                log.info("无法获取有效cookie");
                return;
            }

            for (BaiduKeywordEntity entity : list) {
                try {
                    String[] keywords = {entity.getKeyword()};
                    List<BaiduZhihuEntity> listRaw = baiduService.searchKeyword(Arrays.asList(keywords), entity.getMain());
                    baiduService.parseZhihuLink(listRaw);
                    baiduService.saveList(entity.getMain(), listRaw);
                } catch (Exception e) {
                    //e.printStackTrace();
                    log.error("关键词 {} 异常", entity.getKeyword());
                }
                baiduKeywordService.updateCount(entity);
            }
        }else {
            log.info("暂无待处理关键词");
        }
    }
}
