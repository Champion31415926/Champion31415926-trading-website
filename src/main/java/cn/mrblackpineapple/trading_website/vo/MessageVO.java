package cn.mrblackpineapple.trading_website.vo;

import cn.mrblackpineapple.trading_website.model.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageVO extends Message {

    private String userName;

    private String userAccount;

    private String userAvatar;

}