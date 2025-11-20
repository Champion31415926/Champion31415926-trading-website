package cn.mrblackpineapple.trading_website.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentChildVO {
    private Integer id;

    private Integer parentId;

    private Integer userId;

    private String userName;

    private String userAvatar;

    private Integer replierId;

    private String replierName;

    private String replierAvatar;

    private String content;

    private Boolean replyInputStatus;

    private Boolean upvoteFlag;

    private Integer upvoteCount;

    private String contentType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}