package top.duwd.fintech.sc.zhihu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.dutil.http.RequestBuilder;

@Service
@Slf4j
public class ZhihuQuestionService {
    @Autowired
    private RequestBuilder requestBuilder;

}
