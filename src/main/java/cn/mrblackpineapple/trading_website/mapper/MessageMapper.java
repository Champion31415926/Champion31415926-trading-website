package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.MessageRequest;
import cn.mrblackpineapple.trading_website.model.Message;
import cn.mrblackpineapple.trading_website.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    int save(Message message);

    int setRead(@Param(value = "userId") Integer userId);

    List<MessageVO> query(MessageRequest messageRequest);

    int queryCount(MessageRequest messageRequest);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

}