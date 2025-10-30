package cn.mrblackpineapple.trading_website.common.enums;

import lombok.*;

@Getter
@AllArgsConstructor
public enum AuditStatusEnum {

    NO_AUDIT(1, "Not Audited"),
    HAVE_AUDIT(2, "Audited");

    private final Integer status;
    private final String detail;
}