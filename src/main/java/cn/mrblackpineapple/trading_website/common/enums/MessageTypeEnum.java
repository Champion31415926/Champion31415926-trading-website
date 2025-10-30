package cn.mrblackpineapple.trading_website.common.enums;

import lombok.*;

@AllArgsConstructor
@Getter
public enum MessageTypeEnum {

    EVALUATIONS_BY_REPLY(1,"Comment"),
    EVALUATIONS_BY_UPVOTE(2,"Upvote"),
    DATA_MESSAGE(3,"Metric Alert"),
    SYSTEM_INFO(4,"System Notification");

    private final Integer type;

    private final String detail;

}
