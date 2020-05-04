package top.duwd.fintech.sc.sub.job;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.domain.sub.entity.SubQuestionEntity;
import top.duwd.fintech.common.proxy.ProxyService;
import top.duwd.fintech.sc.sub.service.SubQuestionDetailService;
import top.duwd.fintech.sc.sub.service.SubQuestionService;
import top.duwd.fintech.sc.sub.service.SubUserService;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class SubJob {
    @Autowired
    private SubUserService subUserService;
    @Autowired
    private SubQuestionService subQuestionService;
    @Autowired
    private SubQuestionDetailService subQuestionDetailService;

    @Autowired
    private ProxyService proxyService;
    @Autowired
    private RequestBuilder requestBuilder;

    public static int qid = 333995687;
    public static int count = 0;


    @Autowired
    @Qualifier("SubExecutor")
    Executor subExecutor;

    @Scheduled(fixedRate = 200)
    public void job() {
        //获取 数据库 所有未处理数据
        List<SubQuestionEntity> questionList = subQuestionService.findListByKV("valid", SubQuestionService.SUB_QUESTION_VALID);

    }

    class A implements Runnable {

        @Override
        public void run() {

            HashMap<String, String> map = new HashMap<>();
            map.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");

            Proxy proxy = proxyService.getProxy(1);
            String url = "https://www.zhihu.com/question/" + qid;
            try {
                String withProxy = requestBuilder.getWithProxy(url, map, proxy);
                Document doc = Jsoup.parse(withProxy);
                if ("安全验证 - 知乎".equalsIgnoreCase(doc.title())) {
                    log.info("需要更换IP={}", proxy.address().toString());
                } else {
                    log.info(doc.title() + "" + qid);
                }
            } catch (SocketTimeoutException e) {
                log.error("connect timed out {}", e.getMessage());
                //更换ip  连续超时5次 更换
            } catch (IOException e) {
                log.error("java.io.IOException {}", e.getMessage());
                //connection 403
            } catch (Exception e) {
                e.printStackTrace();
                log.error("系统异常");
            }

        }
    }


}
