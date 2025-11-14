package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class InteractionRequest extends Request {

    private Integer userId;

    private Integer productId;

    private Integer type;
}