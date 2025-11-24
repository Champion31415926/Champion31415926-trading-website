package cn.mrblackpineapple.trading_website.vo;

import cn.mrblackpineapple.trading_website.model.OperationLog;
import lombok.*;
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogVO extends OperationLog {

    private String userName;

    private String userAccount;
}