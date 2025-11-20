package cn.mrblackpineapple.trading_website.interceptor;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.Writer;

public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestMethod = request.getMethod();
        if ("OPTIONS".equals(requestMethod)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/login") ||
                requestURI.contains("/error") ||
                requestURI.contains("/file") ||
                requestURI.contains("/query") && !requestURI.contains("/queryUser")
                        && !requestURI.contains("/queryOrdersList")
                        && !requestURI.contains("/queryProductInfo") ||
                requestURI.contains("/register")) {
            return true;
        }
        String token = request.getHeader("token");
        Claims claims = JwtUtil.fromToken(token);
        if (claims == null) {
            Result<String> error = ApiResult.error("Authentication failed, please login first.");
            response.setContentType("application/json;charset=UTF-8");
            Writer stream = response.getWriter();
            ObjectMapper objectMapper = new ObjectMapper();
            stream.write(objectMapper.writeValueAsString(error));
            stream.flush();
            stream.close();
            return false;
        }
        Integer userId = claims.get("id", Integer.class);
        Integer roleId = claims.get("role", Integer.class);
        LocalThreadHolder.setUserId(userId, roleId);
        return true;
    }
}