package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.OperationLogRequest;
import cn.mrblackpineapple.trading_website.mapper.OperationLogMapper;
import cn.mrblackpineapple.trading_website.model.OperationLog;
import cn.mrblackpineapple.trading_website.service.OperationLogService;
import cn.mrblackpineapple.trading_website.vo.OperationLogVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public Result<String> save(OperationLog operationLog) {
        operationLogMapper.save(operationLog);
        return ApiResult.success("Operation log added successfully.");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        operationLogMapper.batchDelete(ids);
        return ApiResult.success("Operation logs deleted successfully.");
    }

    @Override
    public Result<List<OperationLogVO>> query(OperationLogRequest operationLogRequest) {
        int totalCount = operationLogMapper.queryCount(operationLogRequest);
        List<OperationLogVO> operationLogList = operationLogMapper.query(operationLogRequest);
        return ApiResult.success(operationLogList, totalCount);
    }
}