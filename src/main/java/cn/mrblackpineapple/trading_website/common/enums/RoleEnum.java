package cn.mrblackpineapple.trading_website.common.enums;

import lombok.*;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN(1, "Administrator"),
    USER(2, "User");

    private final Integer role;
    private final String name;

    public static String ROLE(Integer role) {
        for (RoleEnum value : RoleEnum.values()) {
            if (value.getRole().equals(role)) {
                return value.name;
            }
        }
        return null;
    }

}
