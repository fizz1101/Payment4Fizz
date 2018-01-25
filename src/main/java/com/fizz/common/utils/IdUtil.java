package com.fizz.common.utils;

public class IdUtil {

    private static SnowflakeIdFactory ourInstance;

    private static SnowflakeIdFactory getInstance() {
        if (ourInstance == null) {
            ourInstance = new SnowflakeIdFactory(0, 0);
        }
        return ourInstance;
    }

    public static Long getId() {
        return getInstance().nextId();
    }
}
