package cn.mrblackpineapple.trading_website.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public enum ResultCode {
    REQUEST_SUCCESS(200),
    REQUEST_ERROR(400);

    private Integer code;

    @Override
    public String toString() {
        return "ResultCode{" +
                "code=" + code +
                '}';
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}

