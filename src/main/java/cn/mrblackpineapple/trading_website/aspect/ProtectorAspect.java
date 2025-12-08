package cn.mrblackpineapple.trading_website.aspect;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class ProtectorAspect {

    @Around("@annotation(cn.mrblackpineapple.trading_website.aspect.Protector)")
    public Object auth(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return proceedingJoinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        String token = request.getHeader("token");

        if (token == null) {
            return ApiResult.error("Authentication failed, token missing. Please log in first.");
        }
        Claims claims = JwtUtil.fromToken(token);
        if (claims == null) {
            return ApiResult.error("Authentication failed, token invalid or expired. Please log in first.");
        }

        Integer userId = claims.get("id", Integer.class);
        Integer roleId = claims.get("role", Integer.class);

        if (userId == null || roleId == null) {
            return ApiResult.error("Authentication failed, user data incomplete in token.");
        }

        LocalThreadHolder.setUserId(userId, roleId);

        String userRoleName = LocalThreadHolder.getRoleName();

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        Protector protectorAnnotation = method.getAnnotation(Protector.class);

        if (protectorAnnotation == null) {
            return ApiResult.error("System error: Missing Protector annotation definition.");
        }

        String requiredRole = protectorAnnotation.role();

        if (!"".equals(requiredRole)) {
            if (userRoleName == null || !Objects.equals(userRoleName, requiredRole)) {
                LocalThreadHolder.clear();
                return ApiResult.error("No operation permission. Required role: " + requiredRole);
            }
        }

        Object result = proceedingJoinPoint.proceed();

        LocalThreadHolder.clear();

        return result;
    }
}