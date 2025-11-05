package cn.mrblackpineapple.trading_website.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginUpdate {

    private String userAccount;

    private String userPwd;
}