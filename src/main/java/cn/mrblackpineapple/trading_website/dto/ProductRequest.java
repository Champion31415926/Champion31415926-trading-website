package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)

public class ProductRequest extends Request {

    private String name;

    private Integer categoryId;

    private Integer userId;

    private Boolean isBargain;
}
