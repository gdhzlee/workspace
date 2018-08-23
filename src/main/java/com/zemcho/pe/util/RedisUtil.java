package com.zemcho.pe.util;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.Semaphore;

public class RedisUtil {

    private final static Semaphore semaphore = new Semaphore(1);

    public static boolean readCourseSurplus(RedisTemplate<String,Integer> redisTemplate, String courseKey){
        boolean var1 = false;
//        try {
//            semaphore.acquire();
//            Integer surplus = redisTemplate.opsForValue().get(courseKey);
//            if (surplus != null && surplus > 0){
//                var1 = true;
//                redisTemplate.opsForValue().set(courseKey, surplus - 1);
//            }
//            semaphore.release();
//        }catch (InterruptedException e){
//            e.printStackTrace();
//
//        }
        Integer surplus = redisTemplate.opsForValue().get(courseKey);
        System.out.println(surplus);
        if (surplus != null && surplus > 0){
            var1 = true;
//            redisTemplate.opsForValue().set(courseKey, surplus - 1);
        }

        return var1;
    }
}
