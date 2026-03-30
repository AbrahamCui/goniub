package com.rocket.util;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author cjl
 * &#064;description  雪花算法ID生成器
 * 结构：0 - 41位时间戳 - 5位数据中心ID - 5位工作ID - 12位序列号
 * @project gpt-so-vits-v2-automation
 * @dateTime 2026-03-08 12:11:43
 * @version 1.0
 */
public class SnowflakeIdGeneratorUtil {
    private static final SnowflakeIdGeneratorUtil idGenerator = new SnowflakeIdGeneratorUtil();
    /**
     * 默认epoch：2024-01-01 00:00:00 UTC
     */
    public static final long DEFAULT_EPOCH = 1704067200000L;

    /**
     * 默认工作ID
     */
    public static final long DEFAULT_WORKER_ID = 1L;

    /**
     * 默认数据中心ID
     */
    public static final long DEFAULT_DATACENTER_ID = 1L;

    // 起始时间戳 (毫秒)
    @Getter
    private final long epoch;

    // 数据中心ID位数
    private final long datacenterIdBits = 5L;
    // 工作ID位数
    private final long workerIdBits = 5L;
    // 序列号位数
    private final long sequenceBits = 12L;

    // 最大数据中心ID (31)
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    // 最大工作ID (31)
    private final long maxWorkerId = ~(-1L << workerIdBits);
    // 序列号掩码 (4095)
    private final long sequenceMask = ~(-1L << sequenceBits);

    // 工作ID左移位数
    private final long workerIdShift = sequenceBits;
    // 数据中心ID左移位数
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    // 时间戳左移位数
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    private final long workerId;
    private final long datacenterId;

    // 使用AtomicLong确保线程安全
    private final AtomicLong sequence = new AtomicLong(0);
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);

    // 时钟回退检测标志
    private final AtomicBoolean clockBackwardsDetected = new AtomicBoolean(false);

    /**
     * 构造函数 - 使用默认配置
     */
    private SnowflakeIdGeneratorUtil() {
        this(DEFAULT_EPOCH, DEFAULT_WORKER_ID, DEFAULT_DATACENTER_ID);
    }

    /**
     * 构造函数 - 自定义工作ID和数据中心ID
     * @param workerId 工作ID (0-31)
     * @param datacenterId 数据中心ID (0-31)
     */
    private SnowflakeIdGeneratorUtil(long workerId, long datacenterId) {
        this(DEFAULT_EPOCH, workerId, datacenterId);
    }

    /**
     * 构造函数 - 自定义所有参数
     * @param epoch 起始时间戳 (毫秒)
     * @param workerId 工作ID (0-31)
     * @param datacenterId 数据中心ID (0-31)
     */
    private SnowflakeIdGeneratorUtil(long epoch, long workerId, long datacenterId) {
        // 验证epoch
        if (epoch <= 0 || epoch > System.currentTimeMillis()) {
            throw new IllegalArgumentException("Epoch must be positive and not greater than current time");
        }

        // 验证工作ID和数据中心ID
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException(String.format("Worker ID must be between 0 and %d", maxWorkerId));
        }

        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException(String.format("Datacenter ID must be between 0 and %d", maxDatacenterId));
        }

        this.epoch = epoch;
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public static long getNewId() {
        return idGenerator.nextId();
    }

    /**
     * 生成下一个ID（线程安全）
     * @return 雪花算法ID
     * @throws RuntimeException 如果时钟回退
     */
    private long nextId() {
        long timestamp = timeGen();
        long lastTime;

        // 处理时钟回退
        if (timestamp < lastTimestamp.get()) {
            if (clockBackwardsDetected.compareAndSet(false, true)) {
                // 第一次检测到时钟回退，记录日志
                System.err.printf("Clock moved backwards. Current time: %d, last time: %d%n", timestamp, lastTimestamp.get());
            }
            // 阻塞直到时间赶上
            timestamp = waitUntilTimeCatchesUp(lastTimestamp.get());
            clockBackwardsDetected.set(false);
        }

        // 处理同一时间戳内的序列号
        if ((lastTime = lastTimestamp.get()) == timestamp) {
            // 序列号递增，使用循环确保原子性
            long seq;
            while (true) {
                seq = sequence.get();
                if (((seq + 1) & sequenceMask) == seq + 1) {
                    if (sequence.compareAndSet(seq, seq + 1)) {
                        break;
                    }
                } else {
                    // 序列号用尽，等待下一个毫秒
                    timestamp = waitUntilNextMillis(lastTime);
                    sequence.set(0);
                    break;
                }
            }
        } else {
            // 新的时间戳，重置序列号
            sequence.set(0);
        }

        // 更新最后时间戳
        lastTimestamp.set(timestamp);

        // 生成ID
        return ((timestamp - epoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence.get();
    }

    /**
     * 生成下一个ID（线程安全版本，保持与原方法名兼容）
     * @return 雪花算法ID
     */
    public long nextIdThreadSafe() {
        return nextId();
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
            // 添加微小延迟，避免CPU忙等
            Thread.yield();
        }
        return timestamp;
    }

    /**
     * 等待直到时间赶上上次时间戳（处理时钟回退）
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long waitUntilTimeCatchesUp(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp < lastTimestamp) {
            timestamp = timeGen();
            // 添加延迟，避免CPU忙等
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return timestamp;
    }

    /**
     * 返回当前时间，以毫秒为单位
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 解析雪花算法ID
     * @param id 雪花算法ID
     * @return 解析结果
     */
    public SnowflakeId parse(long id) {
        long timestamp = (id >> timestampLeftShift) + epoch;
        long datacenterId = (id >> datacenterIdShift) & maxDatacenterId;
        long workerId = (id >> workerIdBits) & maxWorkerId;
        long sequence = id & sequenceMask;

        return new SnowflakeId(timestamp, datacenterId, workerId, sequence);
    }

    /**
     * 雪花算法ID解析结果
     */
    public static class SnowflakeId {
        private final long timestamp;
        private final long datacenterId;
        private final long workerId;
        private final long sequence;

        public SnowflakeId(long timestamp, long datacenterId, long workerId, long sequence) {
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getDatacenterId() {
            return datacenterId;
        }

        public long getWorkerId() {
            return workerId;
        }

        public long getSequence() {
            return sequence;
        }

        @Override
        public String toString() {
            return String.format("SnowflakeId{timestamp=%d, datacenterId=%d, workerId=%d, sequence=%d}", timestamp, datacenterId, workerId, sequence);
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        SnowflakeIdGeneratorUtil idGenerator = new SnowflakeIdGeneratorUtil();

        System.out.println("Epoch: " + idGenerator.getEpoch());
        System.out.println("Max Datacenter ID: " + idGenerator.maxDatacenterId);
        System.out.println("Max Worker ID: " + idGenerator.maxWorkerId);
        System.out.println("Sequence Mask: " + idGenerator.sequenceMask);

        // 测试单线程生成
        System.out.println("\nSingle thread test:");
        for (int i = 0; i < 10; i++) {
            idGenerator = new SnowflakeIdGeneratorUtil();
            long id = idGenerator.nextId();
            SnowflakeId parsed = idGenerator.parse(id);
            System.out.printf("ID: %d -> %s%n", id, parsed);
        }

        // 测试多线程生成
        System.out.println("\nMulti-thread test:");
        Runnable task = () -> {
            for (int i = 0; i < 5; i++) {
                SnowflakeIdGeneratorUtil idGenerator2 = new SnowflakeIdGeneratorUtil();
                long id = idGenerator2.nextId();
                System.out.printf("Thread %s generated ID: %d%n", Thread.currentThread().getName(), id);
            }
        };

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task, "Thread-" + i);
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nTest completed successfully!");
    }
}
