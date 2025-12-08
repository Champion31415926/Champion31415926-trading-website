package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.ContentRequest;
import cn.mrblackpineapple.trading_website.model.Content;
import cn.mrblackpineapple.trading_website.vo.ContentVO;

import java.util.List;

public interface ContentService {

    Result<String> save(Content content);

    Result<String> update(Content content);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<ContentVO>> query(ContentRequest contentRequest);

}