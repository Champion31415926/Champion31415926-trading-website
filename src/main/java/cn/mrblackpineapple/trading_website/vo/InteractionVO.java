package cn.mrblackpineapple.trading_website.vo;

import cn.mrblackpineapple.trading_website.model.Interaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InteractionVO extends Interaction {

    private String userName;

    private String userAccount;

    private String productTitle;
}