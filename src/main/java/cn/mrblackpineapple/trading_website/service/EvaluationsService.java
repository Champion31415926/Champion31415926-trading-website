package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.EvaluationsRequest;
import cn.mrblackpineapple.trading_website.model.Evaluations;

import java.util.List;

public interface EvaluationsService {

    Result<Object> insert(Evaluations evaluations);

    Result<Object> list(Integer contentId, String contentType);

    Result<Object> query(EvaluationsRequest evaluationsRequest);

    Result<Object> batchDelete(List<Integer> ids);

    Result<String> delete(Integer id);

    Result<Object> upvoteOperation(Evaluations evaluations);

}
