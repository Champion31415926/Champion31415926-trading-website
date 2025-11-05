package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdate {

    private String userAccount;

    private String userName;

    private String userPwd;

    private String userAvatar;

    private String userEmail;
}