package cn.mrblackpineapple.trading_website.common.api;

import lombok.*;
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResult<T> extends Result<T> {

    private T data;

    private Integer total;

    public PageResult(Integer code) {
        super(code, "Query successful");
    }

    public static <T> Result<T> success(T data, Integer total) {
        PageResult<T> result = new PageResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(data);
        result.setTotal(total);
        return result;
    }

}
