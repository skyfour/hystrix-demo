package com.easemob.demo.hystrixdemo.rest;

import com.easemob.demo.hystrixdemo.service.BookStoreService;
import com.easemob.demo.hystrixdemo.service.GetBookCountHystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author stliu @ apache.org
 */
@RestController
@Slf4j
public class RestDemoController {


    private final BookStoreService bookStoreService;

    @Autowired
    public RestDemoController(BookStoreService bookStoreService) {
        this.bookStoreService = bookStoreService;
    }


    /**
     * 这里提供了一个rest api GET /count-with-hystrix
     *
     * 并且提供了两个参数, 具体含义参见 {@link BookStoreService#getBookCount(int, boolean, boolean)}
     */

    @GetMapping("/count-with-hystrix")
    public long countWithHystrix(@RequestParam(value = "sleep", defaultValue = "0") int sleep,
                                 @RequestParam(value = "randomsleep", defaultValue = "false") boolean randomSleep,
                                 @RequestParam(value = "exception", defaultValue = "false") boolean shouldThrowException) {

        GetBookCountHystrixCommand command = new GetBookCountHystrixCommand(bookStoreService, sleep, randomSleep, shouldThrowException);

        long count = command.execute();


        boolean successfulExecution = command.isSuccessfulExecution();
        boolean isCircuitBreakerOpen = command.isCircuitBreakerOpen();
        boolean isResponseFromFallback = command.isResponseFromFallback();
        boolean isResponseTimeout = command.isResponseTimedOut();
        int executionTimeInMilliseconds = command.getExecutionTimeInMilliseconds();


        log.debug("running into /count-with-hystrix api call, \n\t 这个command是否成功执行 : {}" +
                        " \n\t 当前断路器是否打开: {} " +
                        "\n\t 返回值是否走的fallback: {} " +
                        "\n\t 请求是否超时: {}"
                        + "\n\t 这个command的执行时间是 {}毫秒",
                successfulExecution, isCircuitBreakerOpen, isResponseFromFallback, isResponseTimeout, executionTimeInMilliseconds);

        return count;
    }

}
