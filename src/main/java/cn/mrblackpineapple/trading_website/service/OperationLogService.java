package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.OperationLogRequest;
import cn.mrblackpineapple.trading_website.model.OperationLog;
import cn.mrblackpineapple.trading_website.vo.OperationLogVO;

import java.util.List;

public interface OperationLogService {

    Result<String> save(OperationLog operationLog);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<OperationLogVO>> query(OperationLogRequest operationLogRequest);

}