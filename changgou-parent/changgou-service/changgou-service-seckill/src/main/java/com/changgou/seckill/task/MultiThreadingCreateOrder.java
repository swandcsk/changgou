package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import entity.IdWorker;
import entity.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;
    /**
     * 异步执行
     * @Async:该方法会异步执行（底层多线程方式）
     */
    @Async
    public void createOrder(){
        try {
            System.out.println("准备睡会儿再下单！");
            Thread.sleep(10000);
            //从redis队列中获取用户排队信息
            SeckillStatus seckillStatus = (SeckillStatus)redisTemplate.boundListOps("SeckillOrderQueue").rightPop();

            if(seckillStatus==null){
                return;
            }

            //定义要购买的商品的Id和时区以及用户名字
            System.out.println("我都不会等你");
            String time = seckillStatus.getTime();
            Long id = seckillStatus.getGoodsId();
            String username = seckillStatus.getUsername();

            //查询秒杀商品
            String namespace = "SeckillGoods_" + time;
            SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.boundHashOps(namespace).get(id);

            //判断有没有库存
            if(seckillGoods == null || seckillGoods.getStockCount()<=0){
                //没了
                throw new RuntimeException("已售罄!");
            }
            //创建订单对象
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setSeckillId(id);//商品id
            seckillOrder.setMoney(seckillGoods.getCostPrice());//支付金额
            seckillOrder.setUserId(username);//用户名
            seckillOrder.setCreateTime(new Date());//创建时间
            seckillOrder.setStatus("0");//未支付

            /**
             * 将订单对象存储起来
             * 1.一个用户只允许有一个未支付秒杀订单
             * 2.订单存入到redis
             * Hash
             *      namespace->SeckillOrder
             *                         username:SeckillOrder
             *
             */
            redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);

            /**
             * 库存递减
             *      Redis.stockCount--
             * 商品有可能是最后一个，如果是最后一个，则将redis中商品信息删除，并且将redis中该商品的数据同步到mysql
             */
            seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
            if(seckillGoods.getStockCount()<=0){
                //同步数据到mysql
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                //删除redis中的商品数据
                redisTemplate.boundHashOps(namespace).delete(id);
            }else{
                //同步数据到redis
                redisTemplate.boundHashOps(namespace).put(id,seckillGoods);
            }
            //更新下单状态
            seckillStatus.setOrderId(seckillOrder.getId());//id
            seckillStatus.setMoney(Float.valueOf(seckillGoods.getCostPrice()));//支付金额
            seckillStatus.setStatus(2);//待付款
            redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);

            System.out.println("下单完成");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
