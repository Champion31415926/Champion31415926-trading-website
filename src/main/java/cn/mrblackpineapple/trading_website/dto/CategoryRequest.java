package cn.mrblackpineapple.trading_website.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryRequest extends Request {

    private String name;

    private Boolean isUse;
}