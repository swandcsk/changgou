package com.changgou.order.mq.queue;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 延时队列配置
 */
@Configuration
public class QueueConfig {
    /**
     * 创建Queue1 延时队列，会过期，过期后将数据发给Queue2
     *
     */
    @Bean
    public Queue orderDelayQueue(){
        return QueueBuilder
                .durable("orderDelayQueue")
                .withArgument("x-dead-letter-exchange","orderListenerExchange")//orderDelayQueue队列信息会过期，过期后，进入到死信队列，死信队列数据绑定到其他交换机
                .withArgument("x-dead-letter-routing-key","orderListenerQueue")
                .build();

    }

    /**
     * 创建Queue2
     */
    @Bean
    public Queue orderListenerQueue(){
        return new Queue("orderListenerQueue",true);
    }


    /**
     * 创建交换机
     */
    @Bean
    public Exchange orderListenerExchange(){
        return new DirectExchange("orderListenerExchange",true,false);
    }


    /**
     * 队列Queue2绑定交换机
     */
    @Bean
    public Binding orderListenerBinding(Queue orderListenerQueue,Exchange orderListenerExchange){
        return BindingBuilder.bind(orderListenerQueue).to(orderListenerExchange).with("orderListenerQueue").noargs();
    }

}
