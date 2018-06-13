package com.easemob.demo.hystrixdemo.service;


import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

/**
 * 模拟一个book store 的服务, 可以理解成这个会查数据库, 提供了两种模拟参数
 *
 * 1. 这个方法的执行时间(例如查询数据库的耗时)
 * 2. 这个方法可能抛出异常(例如依赖的底层资源, 比如redis或者db连不上的情况)
 *
 *
 * @author stliu @ apache.org
 */
@Component
@Slf4j
public class BookStoreServiceImpl implements BookStoreService {
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public long getBookCount(int sleepInMillseconds,boolean randomSleep, boolean shouldThrowException) {
        //如果设置了模拟延时, 那么这里通过sleep来模拟这个方法的执行时间
        if(randomSleep){
            //如果设置了随机延迟, 那么随机生成一个在0到500之间的数字作为延迟的毫秒数
            sleepInMillseconds = new Random().nextInt(500);
        }
        if (sleepInMillseconds > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(sleepInMillseconds);
            } catch (InterruptedException e) {
                log.error("sleep in getBookCount was interrupted");
            }
        }
        //如果设置了模拟异常, 那么这里抛出异常
        if (shouldThrowException) {
            throw new RuntimeException("throw exception as asked");
        }
        return counter.incrementAndGet();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int kk= new Random().nextInt(5000);
            System.out.println(kk);
        }
    }
}
