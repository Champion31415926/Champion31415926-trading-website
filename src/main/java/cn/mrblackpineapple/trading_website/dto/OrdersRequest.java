package cn.mrblackpineapple.trading_website.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrdersRequest extends Request {

    private String code;

    private String detail;

    private Integer userId;

    private Integer productId;

    //1: Pending payment; 2: Paid; 3: Cancelled
    private Integer tradeStatus;

    //1: No refund; 2: Refund application; 3: Refund rejection; 4: Refund completed
    private Integer refundStatus;

    private List<Integer> productIds;

}
