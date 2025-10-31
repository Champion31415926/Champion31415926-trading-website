package cn.mrblackpineapple.trading_website.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder

public class Request {

    private Integer id;

    private Integer current;

    private Integer size;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
