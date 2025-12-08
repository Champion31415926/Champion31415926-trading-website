package cn.mrblackpineapple.trading_website.aspect;

import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.mapper.OperationLogMapper;
import cn.mrblackpineapple.trading_website.model.OperationLog;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class LogAspect {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Around("@annotation(log)")
    public Object handlePageableParams(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        OperationLog logEntity = createLogEntity(log);
        operationLogMapper.save(logEntity);
        return joinPoint.proceed();
    }

    private OperationLog createLogEntity(Log log) {
        OperationLog operationLog = new OperationLog();
        operationLog.setCreateTime(LocalDateTime.now());
        operationLog.setDetail(log.detail());
        operationLog.setUserId(LocalThreadHolder.getUserId());
        return operationLog;
    }

}