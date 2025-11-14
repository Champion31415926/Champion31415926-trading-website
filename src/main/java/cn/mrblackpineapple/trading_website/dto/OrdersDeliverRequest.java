package cn.mrblackpineapple.trading_website.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrdersDeliverRequest {

    @JsonProperty("ordersId")
    private Integer orderId;

    @JsonProperty("deliveryAdrId")
    private Integer deliveryAddressId;

}