package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogRequest extends Request {

    private Integer userId;

    private String detail;
}