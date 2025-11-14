package cn.mrblackpineapple.trading_website.vo;

import cn.mrblackpineapple.trading_website.model.Orders;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrdersVO extends Orders {

    private String userName;

    private String userAvatar;

    private String productTitle;

    private String productCover;

    private String contactPerson;

    private String contactPhone;

    private String addressDetail;

    private String sellerContactName;

    private String sellerContactPhone;

    private String sellerAddressDetail;
}
