package com.changgou.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import entity.SeckillStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 秒杀订单监听
 */
@Component
@RabbitListener(queues = "seckillQueue")
public class DelaySeckillMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 消息监听
     * @param message
     */
    @RabbitListener
    public void getMessage(String message){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("回滚时间："+simpleDateFormat.format(new Date()));
            //获取用户的排队信息
            SeckillStatus seckillStatus = JSON.parseObject(message,SeckillStatus.class);

            //如果此时Redis中没有用户排队信息，则表名该订单已经处理，如果有，则表示用户尚未完成支付，关闭订单【关闭微信支付】
            Object userQueueStatus = redisTemplate.boundHashOps("UserQueueStatus").get(seckillStatus.getUsername());
            if(userQueueStatus != null){
                //关闭微信支付

                //删除订单
                seckillOrderService.deleteOrder(seckillStatus.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
