package com.easemob.demo.hystrixdemo.service;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

/**
 * @author stliu @ apache.org
 */
public class GetBookCountHystrixCommand extends HystrixCommand<Long> {

    private static final Setter commandSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("bookStoreCommandGroup"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("bookStoreServiceGetCount"));

    private final BookStoreService bookStoreService;
    private final int sleepInMillseconds;
    private final boolean randomSleep;
    private final boolean shouldThrowException;
    public GetBookCountHystrixCommand(BookStoreService bookStoreService, int sleepInMillseconds,boolean randomSleep, boolean shouldThrowException){
        super(commandSetter);
        this.bookStoreService = bookStoreService;
        this.sleepInMillseconds = sleepInMillseconds;
        this.randomSleep = randomSleep;
        this.shouldThrowException = shouldThrowException;
    }



    @Override
    protected Long run() throws Exception {
        return bookStoreService.getBookCount(sleepInMillseconds,randomSleep, shouldThrowException);
    }

    @Override
    protected Long getFallback() {
        return -1L;
    }
}
