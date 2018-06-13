package com.easemob.demo.hystrixdemo.db;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author stliu @ apache.org
 */
public class DBSimulator {
    /**
     * 使用信号量来模拟数据库的并发情况(也可以理解成cpu负载)
     */
    private static final Semaphore semp = new Semaphore(100);


    <T> T query(int queryTimeout, TimeUnit queryTimeoutUnit, int cpuResource, int queryExecutionTime, TimeUnit queryExecutionTimeUnit, T expectedValue) {


        try {

            semp.tryAcquire(cpuResource, queryTimeout, queryTimeoutUnit);

            queryExecutionTimeUnit.sleep(queryExecutionTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semp.release(cpuResource);
        }

        return expectedValue;
    }

}
