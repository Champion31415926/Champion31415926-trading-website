package cn.mrblackpineapple.trading_website.common.util;

import java.util.UUID;

public class IdFactoryUtil {
    public static String generateFileId() {
        return UUID.randomUUID().toString().substring(1, 8);
    }
}