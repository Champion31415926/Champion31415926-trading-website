package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.ContentRequest;
import cn.mrblackpineapple.trading_website.mapper.ContentMapper;
import cn.mrblackpineapple.trading_website.model.Content;
import cn.mrblackpineapple.trading_website.service.ContentService;
import cn.mrblackpineapple.trading_website.vo.ContentVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Resource
    private ContentMapper contentMapper;

    @Override
    public Result<String> save(Content content) {
        if (!StringUtils.hasText(content.getName())) {
            return ApiResult.error("Title cannot be empty");
        }
        if (!StringUtils.hasText(content.getCover())) {
            return ApiResult.error("Please upload a cover image");
        }
        content.setUserId(LocalThreadHolder.getUserId());
        content.setCreateTime(LocalDateTime.now());
        contentMapper.save(content);
        return ApiResult.success("Content added successfully");
    }

    @Override
    public Result<String> update(Content content) {
        if (!StringUtils.hasText(content.getName())) {
            return ApiResult.error("Title cannot be empty");
        }
        if (!StringUtils.hasText(content.getCover())) {
            return ApiResult.error("Please upload a cover image");
        }
        contentMapper.update(content);
        return ApiResult.success("Content updated successfully");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        contentMapper.batchDelete(ids);
        return ApiResult.success("Content deleted successfully");
    }

    @Override
    public Result<List<ContentVO>> query(ContentRequest contentRequest) {
        int totalCount = contentMapper.queryCount(contentRequest);
        List<ContentVO> contentVOS = contentMapper.query(contentRequest);
        return ApiResult.success(contentVOS, totalCount);
    }
}