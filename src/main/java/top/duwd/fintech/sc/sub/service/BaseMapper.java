package top.duwd.fintech.sc.sub.service;

import java.util.List;
import java.util.Map;

interface BaseMapper<T> {

    List<T> findListByKV(String k, Object v);

    List<T> findListByMap(Map<String,Object> map);

}
