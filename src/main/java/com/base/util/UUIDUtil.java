package com.base.util;

import java.util.UUID;

public class UUIDUtil {
    public UUIDUtil() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
