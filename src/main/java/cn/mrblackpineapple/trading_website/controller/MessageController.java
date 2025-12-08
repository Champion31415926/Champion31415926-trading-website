package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.MessageRequest;
import cn.mrblackpineapple.trading_website.service.MessageService;
import cn.mrblackpineapple.trading_website.vo.MessageVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Protector(role = "Administrator")
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return messageService.batchDelete(ids);
    }

    @Protector
    @PostMapping(value = "/setRead")
    public Result<String> setRead() {
        return messageService.setRead(LocalThreadHolder.getUserId());
    }

    @Protector
    @Pager
    @PostMapping(value = "/query")
    public Result<List<MessageVO>> query(@RequestBody MessageRequest messageRequest) {
        return messageService.query(messageRequest);
    }

    @Protector
    @PostMapping(value = "/queryUser")
    public Result<List<MessageVO>> queryUser(@RequestBody MessageRequest messageRequest) {
        messageRequest.setUserId(LocalThreadHolder.getUserId());
        return messageService.query(messageRequest);
    }
}