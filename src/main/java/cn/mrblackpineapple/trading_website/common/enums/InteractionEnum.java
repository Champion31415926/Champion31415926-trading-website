package cn.mrblackpineapple.trading_website.common.enums;

import lombok.*;

@Getter
@AllArgsConstructor
public enum InteractionEnum {

    SAVE(1, "Save/Favorite"),
    VIEW(2, "View/Browse"),
    LOVE(3, "Love/Interested");

    private final Integer type;
    private final String detail;
}
