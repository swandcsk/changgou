package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 搜索
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String,String> searchMap){
        return skuService.search(searchMap);
    }



    /**
     * 数据导入
     * @return
     */
    @GetMapping(value = "/import")
    public Result importData(){
        skuService.importData();
        return new Result(true, StatusCode.OK,"执行操作成功!");
    }


}
