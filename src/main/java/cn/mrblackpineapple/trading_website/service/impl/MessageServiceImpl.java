package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.MessageRequest;
import cn.mrblackpineapple.trading_website.mapper.MessageMapper;
import cn.mrblackpineapple.trading_website.model.Message;
import cn.mrblackpineapple.trading_website.service.MessageService;
import cn.mrblackpineapple.trading_website.vo.MessageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Override
    public Result<String> save(Message message) {
        messageMapper.save(message);
        return ApiResult.success("Message added successfully.");
    }

    @Override
    public Result<String> setRead(Integer userId) {
        messageMapper.setRead(userId);
        return ApiResult.success("Messages marked as read.");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        messageMapper.batchDelete(ids);
        return ApiResult.success("Messages deleted successfully.");
    }

    @Override
    public Result<List<MessageVO>> query(MessageRequest messageRequest) {
        int totalCount = messageMapper.queryCount(messageRequest);
        List<MessageVO> messageVOList = messageMapper.query(messageRequest);
        return ApiResult.success(messageVOList, totalCount);
    }
}