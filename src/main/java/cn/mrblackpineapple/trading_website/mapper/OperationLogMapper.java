package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.OperationLogRequest;
import cn.mrblackpineapple.trading_website.model.OperationLog;
import cn.mrblackpineapple.trading_website.vo.OperationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    int save(OperationLog operationLog);

    List<OperationLogVO> query(OperationLogRequest operationLogRequest);

    int queryCount(OperationLogRequest operationLogRequest);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

}