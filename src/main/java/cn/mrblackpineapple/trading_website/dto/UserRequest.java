package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends Request {

    private String userAccount;

    private String userName;

    private String userEmail;

    private Boolean role;

    private Boolean isLogin;

    private Boolean isWord;
}