package cn.mrblackpineapple.trading_website.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PathUtil {
    public static String getClassLoadRootPath() {
        String path = "";
        try {
            String prePath = URLDecoder.decode(PathUtil.class.getClassLoader().getResource("").getPath(),"utf-8");

            if (prePath.endsWith("/")) {
                prePath = prePath.substring(0, prePath.length() - 1);
            }

            if (prePath.contains("/target/classes")) {
                path = prePath.substring(0, prePath.lastIndexOf("/target/classes"));
            } else if (prePath.contains("/build/classes")) {
                path = prePath.substring(0, prePath.lastIndexOf("/build/classes"));
            } else {
                path = prePath;
            }

            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.startsWith("windows") && path.startsWith("/")) {
                path = path.substring(1);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

}