package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.MessageRequest;
import cn.mrblackpineapple.trading_website.model.Message;
import cn.mrblackpineapple.trading_website.vo.MessageVO;

import java.util.List;

public interface MessageService {

    Result<String> save(Message message);

    Result<String> setRead(Integer userId);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<MessageVO>> query(MessageRequest messageRequest);

}