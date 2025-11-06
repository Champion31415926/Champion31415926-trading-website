package cn.mrblackpineapple.trading_website.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {

    private Integer id;

    private String code;

    private String detail;

    private Integer userId;

    private Integer productId;

    private BigDecimal buyPrice;

    private Integer buyNumber;

    private Integer tradeStatus;

    private Integer refundStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tradeTime;

    private Boolean isRefundConfirm;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Integer addressId;

    private Boolean isConfirm;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime isConfirmTime;

    private Boolean isDeliver;

    private Integer deliverAdrId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliverTime;

    public BigDecimal calculateTotalPrice() {
        if (buyPrice == null || buyNumber == null) {
            return BigDecimal.ZERO;
        }
        return buyPrice.multiply(BigDecimal.valueOf(buyNumber))
                .setScale(2, RoundingMode.HALF_UP);
    }

}