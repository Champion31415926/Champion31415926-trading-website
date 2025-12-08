package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.ContentRequest;
import cn.mrblackpineapple.trading_website.model.Content;
import cn.mrblackpineapple.trading_website.service.ContentService;
import cn.mrblackpineapple.trading_website.vo.ContentVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Resource
    private ContentService contentService;

    @Protector
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody Content content) {
        return contentService.save(content);
    }

    @Protector
    @PutMapping(value = "/update")
    public Result<String> update(@RequestBody Content content) {
        return contentService.update(content);
    }

    @Protector
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return contentService.batchDelete(ids);
    }

    @Protector
    @Pager
    @PostMapping(value = "/queryUser")
    public Result<List<ContentVO>> queryUser(@RequestBody ContentRequest contentRequest) {
        contentRequest.setUserId(LocalThreadHolder.getUserId());
        return contentService.query(contentRequest);
    }

    @Pager
    @PostMapping(value = "/query")
    public Result<List<ContentVO>> query(@RequestBody ContentRequest contentRequest) {
        return contentService.query(contentRequest);
    }
}