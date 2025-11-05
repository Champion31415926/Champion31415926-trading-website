package cn.mrblackpineapple.trading_website.common.context;

import cn.mrblackpineapple.trading_website.common.enums.RoleEnum;

import java.util.*;

public class LocalThreadHolder {

    private static final ThreadLocal<Map<String, Integer>> USER_HOLDER = new ThreadLocal<>();

    public static void setUserId(Integer userId, Integer userRole) {
        Map<String, Integer> map = new HashMap<>();
        map.put("userId", userId);
        map.put("userRole", userRole);
        USER_HOLDER.set(map);
    }

    public static Integer getUserId() {
        Map<String, Integer> map = USER_HOLDER.get();
        return map != null ? map.get("userId") : null;
    }

    public static Integer getRoleId() {
        Map<String, Integer> map = USER_HOLDER.get();
        return map != null ? map.get("userRole") : null;
    }

    public static String getRoleName() {
        Integer roleId = getRoleId();
        return roleId != null ? RoleEnum.ROLE(roleId) : null;
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}