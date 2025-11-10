package cn.mrblackpineapple.trading_website.vo;

import cn.mrblackpineapple.trading_website.model.Product;
import lombok.*;
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductVO extends Product {

    private String userName;

    private String userAvatar;

    private String categoryName;

    private Integer likeNumber;

    private Integer saveNumber;

    private Integer viewNumber;
}