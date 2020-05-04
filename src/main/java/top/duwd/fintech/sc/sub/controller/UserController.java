package top.duwd.fintech.sc.sub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.fintech.common.proxy.ProxyService;

@RequestMapping("/sub/user")
@RestController
@Slf4j
public class UserController {


    @PostMapping("/regAndLogin")
    public void regAndLogin(@RequestParam(value = "phone") String phone) {
        //注册并登陆
        //phone 合法性校验

        //检查手机是否注册

        //未注册-发送验证码
        //校验通过-创建用户-登陆


        //已注册-发送验证码 登陆
    }


    @Autowired
    private ProxyService proxyService;
    @Autowired
    private RequestBuilder requestBuilder;


}
