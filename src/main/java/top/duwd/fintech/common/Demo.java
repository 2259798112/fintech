package top.duwd.fintech.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.fintech.common.msg.wx.qiye.WxService;

@RestController
@RequestMapping("/demo")
public class Demo {
    @Autowired
    private WxService wxService;

    @GetMapping("/send")
    public ApiResult demo(){
        wxService.sendText("你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\"http://work.weixin.qq.com\">邮件中心视频实况</a>，聪明避开排队。");
        return null;
    }
}
