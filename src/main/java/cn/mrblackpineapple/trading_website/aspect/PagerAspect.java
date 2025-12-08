package cn.mrblackpineapple.trading_website.aspect;

import cn.mrblackpineapple.trading_website.dto.Request;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class PagerAspect {

    @Around("@annotation(pager)")
    public Object handlePageableParams(ProceedingJoinPoint joinPoint, Pager pager) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Request request) {
                configPager(request);
            }
        }

        return joinPoint.proceed(args);
    }

    private void configPager(Request request) {
        if (request.getCurrent() != null && request.getSize() != null) {
            request.setCurrent((request.getCurrent() - 1) * request.getSize());
        }
    }
}