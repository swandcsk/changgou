package com.changgou.seckill.mq;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 1.延时超时队列->负责数据暂时存储   queue1
 * 2.真正监听的消息队列              queue2
 */
@Configuration
public class QueueConfig {

    /**
     * 延时超时队列->负责数据暂时存储   queue1
     */
    @Bean
    public Queue delaySeckillQueue(){
        return QueueBuilder.durable("delaySeckillQueue")
                .withArgument("x-dead-letter-exchange","seckillExchange")     //当前队列的消息一旦过期，则进入到死信队列
                .withArgument("x-dead-letter-routing-kty","seckillQueue")     //将死信队列的数据路由到指定队列中
                .build();
    }

    /**
     * 延真正监听的消息队列              queue2
     */
    @Bean
    public Queue seckillQueue(){
        return new Queue("seckillQueue");
    }

    /**
     * 秒杀交换机
     * @return
     */
    @Bean
    public Exchange seckillExchange(){
        return new DirectExchange("seckillExchange");
    }

    /**
     * 队列绑定交换机
     */
    @Bean
    public Binding seckillQueueBindingExchange(Queue seckillQueue ,Exchange seckillExchange){
        return BindingBuilder.bind(seckillQueue).to(seckillExchange).with("seckillQueue").noargs();
    }

}
