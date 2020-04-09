package top.duwd.fintech.common.proxy;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.proxy.ProxyUtil;
import top.duwd.fintech.common.domain.proxy.ProxyEntity;
import top.duwd.fintech.common.mapper.proxy.ProxyMapper;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProxyService {
    @Autowired
    private ProxyMapper proxyMapper;

    public Proxy getProxy(int minute) {
        Date date = new Date();
        Example example = new Example(ProxyEntity.class);
        example.createCriteria().andBetween("createTime", DateUtil.addMin(date, -minute), date);
        example.setOrderByClause("id desc");

        List<ProxyEntity> list = proxyMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(list.get(0).getIp(), Integer.parseInt(list.get(0).getPort())));
        } else {
            JSONObject moguProxy = ProxyUtil.getMoguProxy();
            if (moguProxy != null) {
                ProxyEntity proxyEntity = new ProxyEntity();
                String ip = moguProxy.getString("ip");
                String port = moguProxy.getString("port");
                proxyEntity.setPlat("moguproxy.com");
                proxyEntity.setIp(ip);
                proxyEntity.setPort(port);
                proxyEntity.setCreateTime(date);
                proxyMapper.insert(proxyEntity);
                return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, Integer.parseInt(port)));
            } else {
                return null;
            }
        }


    }
}
