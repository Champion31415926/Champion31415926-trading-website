package cn.mrblackpineapple.trading_website.common.util;

import java.util.regex.Pattern;

public class PhoneNumberValidateUtil {

    private static final String CHINA_MOBILE_PATTERN = "^1[3-9]\\d{9}$";

    private static final Pattern PHONE_PATTERN = Pattern.compile(CHINA_MOBILE_PATTERN);

    public static boolean isValidChinesePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    public static void validatePhoneNumber(String phoneNumber) {
        if (!isValidChinesePhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number: " + phoneNumber);
        }
    }

    public static String validateWithDefault(String phoneNumber, String defaultValue) {
        return isValidChinesePhoneNumber(phoneNumber) ? phoneNumber : defaultValue;
    }
}