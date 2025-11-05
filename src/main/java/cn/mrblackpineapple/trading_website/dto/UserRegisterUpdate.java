package cn.mrblackpineapple.trading_website.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterUpdate {

    private String userName;

    private String userAccount;

    private String userPwd;

    private String userEmail;

    private String userAvatar;
}
