package top.duwd.fintech.coin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.duwd.dutil.zendesk.ZendeskService;
import top.duwd.fintech.coin.domain.entity.ZendeskEntity;
import top.duwd.fintech.coin.mapper.ZendeskMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ZenService {

    @Autowired
    private ZendeskMapper mapper;

    public List<ZendeskEntity> check(){
        ZendeskService service = new ZendeskService();
        List<ZendeskEntity> listHb = checkDB(service.getMap(ZendeskService.HB), ZendeskService.HB);
        List<ZendeskEntity> listOk = checkDB(service.getMap(ZendeskService.OKEX), ZendeskService.OKEX);
        List<ZendeskEntity> listBn = checkDB(service.getMap(ZendeskService.BN), ZendeskService.BN);

        List<ZendeskEntity> list = new ArrayList<>();
        if (listHb !=null && !listHb.isEmpty())list.addAll(listHb);
        if (listOk !=null && !listOk.isEmpty())list.addAll(listOk);
        if (listBn !=null && !listBn.isEmpty())list.addAll(listBn);
        return list;
    }

    public List<ZendeskEntity> checkDB(Map<String,String> map, String plat){
        if (map == null || map.isEmpty()){
            return null;
        }

        List<ZendeskEntity> list = new ArrayList<>();
        for (String key : map.keySet()) {
            String id = DigestUtils.md5DigestAsHex(key.getBytes()).toUpperCase();
            ZendeskEntity db = mapper.selectByPrimaryKey(id);
            if (db == null){
                ZendeskEntity entity = new ZendeskEntity();
                entity.setId(id);
                entity.setPlat(plat);
                entity.setTitle(key);
                entity.setLink(map.get(key));
                entity.setCreateDate(new Date());
                mapper.insert(entity);
                list.add(entity);
            }
        }
        return list;
    }


}
