package cn.mrblackpineapple.trading_website.common.enums;

import lombok.*;

@Getter
@AllArgsConstructor
public enum LoginStatusEnum {

    USE(false, "Login Available"),
    BANK_USE(true, "Login Status Exception");

    private final Boolean flag;
    private final String name;

}