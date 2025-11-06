package cn.mrblackpineapple.trading_website.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Category {

    private Integer id;

    private String name;

    private Boolean isUse;

}
