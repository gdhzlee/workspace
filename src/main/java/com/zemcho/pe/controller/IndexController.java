package com.zemcho.pe.controller;

import com.zemcho.pe.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * @Author Jetvin
 * @Date 2018/8/17
 * @Time 10:20
 * @Version ╮(╯▽╰)╭
 *
 * <!--         ░░░░░░░░░░░░░░░░░░░░░░░░▄░░░        -->
 * <!--         ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░░        -->
 * <!--         ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐ ░        -->
 * <!--         ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░        -->
 * <!--         ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐ ░        -->
 * <!--         ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░        -->
 * <!--         ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒ ░        -->
 * <!--         ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░        -->
 * <!--         ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄░        -->
 * <!--         -░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒        -->
 * <!--         ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒░        -->
 * <!--                                             -->
 * <!--                 咦！有人在改BUG               -->
 */
@RestController
public class IndexController {

    @Autowired
    RedisTemplate<String,Integer> redisTemplate;

    @GetMapping("/exception")
    public String exception(){

        throw new RuntimeException("这是假异常");
    }

    @GetMapping("/")
    public String index(){

        return "来自8180的返回";
    }

    private static AtomicInteger count = new AtomicInteger(0);

//    public final static Semaphore semaphore = new Semaphore(10);

    @GetMapping("/course/take")
    public Result takeCourse(@RequestParam(name = "key") String key){
        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, redisTemplate);
        int andDecrement = redisAtomicInteger.decrementAndGet();

        int code = 1000;
        String message = "";
        if (andDecrement >= 0){
            message = "抢课成功，当前课程数 = ";
        }else {
            message = "抢课失败，当前课程数 = ";
        }


        message = "线程" + count.incrementAndGet() +  message + (andDecrement >= 0 ? andDecrement : 0);
        System.out.println(message);

        return new Result(code, message);
    }

//    @GetMapping("test")
//    public void redisTest(@RequestParam(name = "key", required = true) String key,
//                          @RequestParam(name = "client", defaultValue = "5000") int client,
//                          @RequestParam(name = "thread", defaultValue = "40") int thread) throws InterruptedException {
//
//        ExecutorService executorService = Executors.newCachedThreadPool();
//
//        // 信号量 - 当前可共享资源数
//
//
//        // 任务计数器 - 待完成任务数
//        final CountDownLatch countDownLatch = new CountDownLatch(client);
//
//        for (int i =0; i < client; i++){
//            executorService.execute(() ->{
//
//                try {
//                    semaphore.acquire();
//                    int count = getCount();
//                    semaphore.release();
//
//                    RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, redisTemplate);
//                    int andDecrement = redisAtomicInteger.decrementAndGet();
//                    if (andDecrement >= 0){
//                        System.out.println("线程" + count + "抢课成功" + "当前课程数 = " + (andDecrement >= 0 ? andDecrement : 0));
//                    }else {
//                        System.out.println("线程" + count + "抢课失败" + "当前课程数 = " + (andDecrement >= 0 ? andDecrement : 0));
//                    }
//
//                    // 任务数减 1
//                    countDownLatch.countDown();
//
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//
//
//            });
//        }
//
//        // 线程休眠 待完成任务数为0时会被唤醒
//        countDownLatch.await();
//
//        // 关闭线程池
//        executorService.shutdown();
//    }
//
//    private int getCount(){
//        return count.incrementAndGet();
//    }
}
