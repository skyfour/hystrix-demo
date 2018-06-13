package com.easemob.demo.hystrixdemo.service;

/**
 *
 * 一个管理图书库存的服务
 *
 * @author stliu @ apache.org
 */
public interface BookStoreService {

    /**
     * 查询当前的图书库存中已经存在的图书数量
     *
     * @param sleepInMillseconds 这个方法需要使用的时间, 单位是毫秒(由于这个是个模拟程序, 所以使用这个参数来模拟这个方法的耗时)
     * @param randomSleep 如果这个为true, 那么会忽略 {@param sleepInMillseconds}参数, 而会在0-5000毫秒中随机的模拟一个延迟
     * @param shouldThrowException 是否这个方法应该模拟抛出异常
     * @return 返回当前库存的图书数量.
     */
    long getBookCount(int sleepInMillseconds,boolean randomSleep, boolean shouldThrowException);

}
