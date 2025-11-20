package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddressRequest extends Request {
    private Integer userId;

    private String contactPerson;

    private String addressDetail;

    private String contactPhone;

    private Boolean isDefault;

    private Integer offset;

    private Integer limit;

}