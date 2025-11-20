package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class EvaluationsRequest extends Request {

    private Integer userId;

    private String contentType;

    private Integer commenterId;

    private String content;

}