package com.changgou.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.*;
import javafx.event.EventType;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * 实现Mysql数据监听
 */
@CanalEventListener
public class CanalDataEventListener {

    private final ContentFeign contentFeign;

    private final StringRedisTemplate stringRedisTemplate;

    public CanalDataEventListener(ContentFeign contentFeign, StringRedisTemplate stringRedisTemplate) {
        this.contentFeign = contentFeign;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 监听数据变化，将数据写到Redis中
     * @param eventType
     * @param rowData
     */
    @ListenPoint(
            destination = "example",
            schema = "changgou_content",
            table = {"tb_content","tb_content_category"},
            eventType = {
                    CanalEntry.EventType.INSERT,
                    CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE}
    )
    public void onEventListener(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = getColumnValue(eventType,rowData);
        List<Content> contents = contentFeign.findByCategoryId(Long.parseLong(categoryId)).getData();
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(contents));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        if (eventType == CanalEntry.EventType.UPDATE || eventType == CanalEntry.EventType.INSERT) {
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                if ("category_id".equalsIgnoreCase(column.getName())) {
                    return column.getValue();
                }
            }
        }
        if (eventType == CanalEntry.EventType.DELETE) {
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                if ("category_id".equalsIgnoreCase(column.getName())) {
                    return column.getValue();
                }
            }
        }
        return "";
    }


//
//    /**
//     * 增加监听 只有增加后的数据
//     * rowData.getAfterColumnsList():增加/修改
//     * rowData.getBeforeColumnsList():删除/修改
//     * eventType 当前操作的类型,增加数据
//     * 发生变更的一行数据
//     */
//    @InsertListenPoint
//    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
//        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
//            System.out.println("增加后:列名:" + column.getName() + "--------变更的数据:" + column.getValue());
//        }
//    }
//
//    /**
//     * 修改监听
//     */
//    @UpdateListenPoint
//    public void onEventUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
//        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
//            System.out.println("修改前:列名:" + column.getName() + "--------变更的数据:" + column.getValue());
//        }
//
//        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
//            System.out.println("修改后:列名:" + column.getName() + "--------变更的数据:" + column.getValue());
//        }
//
//
//    }
//
//    /**
//     * 删除监听
//     */
//    @DeleteListenPoint
//    public void onEventDelete(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
//        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
//            System.out.println("删除前:列名:" + column.getName() + "--------变更的数据:" + column.getValue());
//        }
//    }
//
//
//
//    /**
//     * 自定义监听
//     */
//    @ListenPoint(
//            eventType = {CanalEntry.EventType.DELETE,CanalEntry.EventType.UPDATE},
//            schema = {"changgou_content"}, //指定监控的数据库
//            table = {"tb_content"},//指定监控的表
//            destination = "example"//指定实例的地址
//    )
//    public void onEventCustomUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
//        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
//            System.out.println("=====自定义操作前:列名:" + column.getName() + "--------变更的数据:" + column.getValue());
//        }
//
//        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
//            System.out.println("=====自定义操作后:列名:" + column.getName() + "--------变更的数据:" + column.getValue());
//        }
//    }

}
