package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.netflix.client.config.IClientConfigKey;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 定时将秒杀商品存入redis缓存
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定时操作
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void loadGoddsPushRedis(){
        /**
         * 1.查询符合当前参与秒杀的时间菜单
         * 2.秒杀商品库存>0 stock_count
         * 3.审核状态->审核通过 status:1
         * 4.开始时间，结束时间
         *  end_time>=当前时间
         *  时间菜单的开始时间=<start_time && end_time <时间菜单的开始时间+2小时
         */
        //求时间菜单
        List<Date> dateMenus = DateUtil.getDateMenus();
        //循环查询每个时间区间的秒杀商品
        for(Date dateMenu:dateMenus){
            //时间的字符串格式yyyyMMddHH
            String timespace = "SeckillGoods_" + DateUtil.date2Str(dateMenu);
            /**
             * 1.秒杀商品库存>0 stock_count
             * 2.审核状态->审核通过 status:1
             * 3.开始时间，结束时间
             *  end_time>=当前时间
             *  时间菜单的开始时间=<start_time && end_time <时间菜单的开始时间+2小时
             */
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //审核状态->审核通过 status:1
            criteria.andEqualTo("status","1");
            //秒杀商品库存>0 stock_count
            criteria.andGreaterThan("stockCount",0);
            //时间菜单的开始时间=<start_time && end_time <时间菜单的开始时间+2小时
            criteria.andGreaterThanOrEqualTo("startTime",dateMenu);
            criteria.andLessThan("endTime",DateUtil.addDateHour(dateMenu,2));

            //排除已经存入到redis中的seckillGoods->1.求出当前命名空间下所有的商品的id（key）2.每次查询排除掉之前存在的商品的key的数据
            Set keys = redisTemplate.boundHashOps(timespace).keys();
            if(keys!=null && keys.size()>0){
                //排除
                criteria.andNotIn("id",keys);
            }

            //查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

            for (SeckillGoods seckillGood : seckillGoods) {
                System.out.println("商品ID："+seckillGood.getId()+"---存入到redis" + timespace);
                //存入到redis
                redisTemplate.boundHashOps(timespace).put(seckillGood.getId(),seckillGood);
                //给每个商品做个队列
                redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillGood.getId()).leftPushAll(putAllIds(seckillGood.getStockCount(),seckillGood.getId()));
            }

        }
    }

    /**
     * 获取每个商品的ID集合
     */
    public  Long[] putAllIds(Integer num,Long id){
        Long[] ids = new Long[num];
        for(int i=0;i<ids.length;i++){
            ids[i] = id;
        }
        return ids;
    }
}
