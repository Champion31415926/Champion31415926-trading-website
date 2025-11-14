package cn.mrblackpineapple.trading_website.dto;

import cn.mrblackpineapple.trading_website.model.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrdersUpdate extends Orders {

    private Integer buyNumber;

}