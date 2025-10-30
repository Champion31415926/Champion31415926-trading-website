package cn.mrblackpineapple.trading_website.common.enums;

import lombok.*;

@Getter
@AllArgsConstructor
public enum WordStatusEnum {

    USE(false, "Available"),
    BANK_USE(true, "Muted");

    private final Boolean flag;
    private final String name;

}