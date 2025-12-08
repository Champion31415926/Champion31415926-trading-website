package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)

public class MessageRequest extends Request {

    private Integer userId;

    private String content;
}