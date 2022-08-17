package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillOrder;
import com.github.pagehelper.PageInfo;
import entity.SeckillStatus;

import java.util.List;
/****
 * @Author:传智播客
 * @Description:SeckillOrder业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillOrderService {

    /**
     * 删除订单
     * @pram username
     */
    void deleteOrder(String username);

    /**
     * 修改订单状态
     * @param username
     * @param transactionid
     * @param endtime
     */
    void updatePayStatus(String username,String transactionid,String endtime);



    /**
     * 抢单状态查询
     * @param username
     *
     */
    SeckillStatus queryStatus(String username);

    /***
     * SeckillOrder多条件分页查询
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size);

    /***
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(int page, int size);

    /***
     * SeckillOrder多条件搜索方法
     * @param seckillOrder
     * @return
     */
    List<SeckillOrder> findList(SeckillOrder seckillOrder);

    /***
     * 删除SeckillOrder
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     */
    void update(SeckillOrder seckillOrder);

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
     SeckillOrder findById(Long id);

    /***
     * 查询所有SeckillOrder
     * @return
     */
    List<SeckillOrder> findAll();

    /**
     * 添加秒杀订单
     * @param time
     * @param id
     * 用户的登录名字
     */
    Boolean add(String time, Long id, String username);
}
