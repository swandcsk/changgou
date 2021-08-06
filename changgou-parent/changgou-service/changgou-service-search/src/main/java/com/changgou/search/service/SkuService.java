package com.changgou.search.service;

import java.util.Map;

public interface SkuService {
    /**
     * 导入数据到索引库中
     */
    void importData();

    /**
     * 条件搜索
     */
    Map<String,Object> search(Map<String,String> searchMap);

}
