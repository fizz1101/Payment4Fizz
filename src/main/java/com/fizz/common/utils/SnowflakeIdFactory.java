package com.fizz.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Twitter_Snowflake 生成唯一ID<br>
 * from: http://blog.csdn.net/antony9118/article/details/52766848<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
@Service
public class SnowflakeIdFactory {

    private static Logger logger = LoggerFactory.getLogger(SnowflakeIdFactory.class);

    //开始该类生成ID的时间截，1288834974657 (Thu, 04 Nov 2010 01:42:54 GMT) 这一时刻到当前时间所经过的毫秒数，占 41 位（还有一位是符号位，永远为 0）。
    private final long startTime = 1463834116272L;

    //机器id所占的位数
    private long workerIdBits = 5L;

    //数据标识id所占的位数
    private long datacenterIdBits = 5L;

    //支持的最大机器id，结果是31,这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数（不信的话可以自己算一下，记住，计算机中存储一个数都是存储的补码，结果是负数要从补码得到原码）
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);

    //支持的最大数据标识id
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    //序列在id中占的位数
    private long sequenceBits = 12L;

    //机器id向左移12位
    private long workerIdLeftShift = sequenceBits;

    //数据标识id向左移17位
    private long datacenterIdLeftShift = workerIdBits + workerIdLeftShift;

    //时间截向左移5+5+12=22位
    private long timestampLeftShift = datacenterIdBits + datacenterIdLeftShift;

    //生成序列的掩码，这里为1111 1111 1111
    private long sequenceMask = -1 ^ (-1 << sequenceBits);

    private long workerId;

    private long datacenterId;

    //同一个时间截内生成的序列数，初始值是0，从0开始
    private long sequence = 0L;

    //上次生成id的时间截
    private long lastTimestamp = -1L;

    /**
     * 生成snowflakeId
     * @return id
     */
    public static String snowflakeId() {
        return (new SnowflakeIdFactory(0,0).nextId()).toString();
    }

    public SnowflakeIdFactory(){}

    /**
     * 构造函数
     * @param workerId 工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeIdFactory(long workerId, long datacenterId){
        if(workerId < 0 || workerId > maxWorkerId){
            throw new IllegalArgumentException(
                    String.format("workerId[%d] is less than 0 or greater than maxWorkerId[%d].", workerId, maxWorkerId));
        }
        if(datacenterId < 0 || datacenterId > maxDatacenterId){
            throw new IllegalArgumentException(
                    String.format("datacenterId[%d] is less than 0 or greater than maxDatacenterId[%d].", datacenterId, maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public synchronized Long nextId(){
        long timestamp = timeGen();
        if(timestamp < lastTimestamp){
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果是同一时间生成的，则自增
        if(timestamp == lastTimestamp){
            sequence = (sequence + 1) & sequenceMask;
            if(sequence == 0){
                //生成下一个毫秒级的序列
                timestamp = tilNextMillis();
                //序列从0开始
                sequence = 0L;
            }
        }else{
            //如果发现是下一个时间单位，则自增序列回0，重新自增
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        //看本文第二部分的结构图，移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - startTime) << timestampLeftShift)
                | (datacenterId << datacenterIdLeftShift)
                | (workerId << workerIdLeftShift)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @return  当前时间戳
     */
    protected long tilNextMillis(){
        long timestamp = timeGen();
        if(timestamp <= lastTimestamp){
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return  当前时间(毫秒)
     */
    protected long timeGen(){
        return System.currentTimeMillis();
    }


    /**
     * case1: 测试每秒生产id个数?
     * */
    public static void testPerSecondProductIdNums(){
        SnowflakeIdFactory idWorker = new SnowflakeIdFactory(1, 2);
        long start = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; System.currentTimeMillis()-start<1000; i++,count=i) {
            /**  测试方法一: 此用法纯粹的生产ID,每秒生产ID个数为300w+ */
            idWorker.nextId();
            /**  测试方法二: 在log中打印,同时获取ID,此用法生产ID的能力受限于log.error()的吞吐能力.
             * 每秒徘徊在10万左右. */
            //log.error("{}",idWorker.nextId());
        }
        long end = System.currentTimeMillis()-start;
        System.out.println(end);
        System.out.println(count);
    }


    /**
     * case2: 单线程-测试多个生产者同时生产N个id,验证id是否有重复?
     * */
    public static void testProductId(int dataCenterId, int workerId, int n){
        SnowflakeIdFactory idWorker = new SnowflakeIdFactory(workerId, dataCenterId);
        SnowflakeIdFactory idWorker2 = new SnowflakeIdFactory(workerId+1, dataCenterId);
        Set<Long> setOne = new HashSet<>();
        Set<Long> setTow = new HashSet<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            setOne.add(idWorker.nextId());//加入set
        }
        long end1 = System.currentTimeMillis() - start;
        logger.info("第一批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}",n,setOne.size(),end1);

        for (int i = 0; i < n; i++) {
            setTow.add(idWorker2.nextId());//加入set
        }
        long end2 = System.currentTimeMillis() - start;
        logger.info("第二批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}",n,setTow.size(),end2);

        setOne.addAll(setTow);
        logger.info("合并总计生成ID个数:{}",setOne.size());

    }

    /**
     * case3: 多线程-测试多个生产者同时生产N个id, 全部id在全局范围内是否会重复?
     * */
    public static void testProductIdByMoreThread(int dataCenterId, int workerId, int n) throws InterruptedException {
        List<Thread> tlist = new ArrayList<>();
        Set<Long> setAll = new HashSet<>();
        CountDownLatch cdLatch = new CountDownLatch(10);
        long start = System.currentTimeMillis();
        int threadNo = dataCenterId;
        Map<String, SnowflakeIdFactory> idFactories = new HashMap<>();
        for(int i=0;i<10;i++){
            //用线程名称做map key.
            idFactories.put("snowflake"+i,new SnowflakeIdFactory(workerId, threadNo++));
        }
        for(int i=0;i<10;i++){
            Thread temp =new Thread(new Runnable() {
                @Override
                public void run() {
                    Set<Long> setId = new HashSet<>();
                    SnowflakeIdFactory idWorker = idFactories.get(Thread.currentThread().getName());
                    for(int j=0;j<n;j++){
                        setId.add(idWorker.nextId());
                    }
                    synchronized (setAll){
                        setAll.addAll(setId);
                        logger.info("{}生产了{}个id,并成功加入到setAll中.",Thread.currentThread().getName(),n);
                    }
                    cdLatch.countDown();
                }
            },"snowflake"+i);
            tlist.add(temp);
        }
        for(int j=0;j<10;j++){
            tlist.get(j).start();
        }
        cdLatch.await();

        long end1 = System.currentTimeMillis() - start;

        logger.info("共耗时:{}毫秒,预期应该生产{}个id, 实际合并总计生成ID个数:{}",end1,10*n,setAll.size());

    }

    public static void main(String[] args) {
        //testPerSecondProductIdNums();

        //testProductId(1, 2, 10000);
        //testProductId(1, 2, 20000);

        try {
            testProductIdByMoreThread(1, 2, 100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*class IdServiceThread implements Runnable {
            private Set<Long> set;
            @Autowired
            private IdWorker idService;

            public IdServiceThread(Set<Long> set, IdWorker idService) {
                this.set = set;
                this.idService = idService;
            }

            @Override
            public void run() {
                while (true) {
                    long id = idService.nextId();
                    System.out.println(String.valueOf(id).substring(6));
                    *//*if (!set.add(id)) {
                        System.out.println("duplicate:" + id);
                    }*//*
                }
            }
        }

        Set<Long> set = new HashSet<Long>();
        IdWorker idWorker = new IdWorker(0, 0);
        for(int i=0;i<3;i++){
            Thread t1 = new Thread(new IdServiceThread(set, new IdWorker(0, 0)));
            t1.setDaemon(true);
            t1.start();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

}