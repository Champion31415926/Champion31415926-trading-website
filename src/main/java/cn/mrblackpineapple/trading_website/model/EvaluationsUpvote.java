package cn.mrblackpineapple.trading_website.model;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class EvaluationsUpvote {

    private Integer id;

    private Integer userId;

    private Integer evaluationsId;
}