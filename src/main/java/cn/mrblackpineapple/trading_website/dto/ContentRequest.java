package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContentRequest extends Request {

    private String name;

    private Integer userId;
}