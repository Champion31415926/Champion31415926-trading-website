package cn.mrblackpineapple.trading_website.vo;

import cn.mrblackpineapple.trading_website.model.Content;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContentVO extends Content {

    private String userName;

    private String userAvatar;
}