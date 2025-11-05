package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.PageResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.common.enums.LoginStatusEnum;
import cn.mrblackpineapple.trading_website.common.enums.RoleEnum;
import cn.mrblackpineapple.trading_website.common.enums.WordStatusEnum;
import cn.mrblackpineapple.trading_website.common.util.JwtUtil;
import cn.mrblackpineapple.trading_website.dto.UserLoginUpdate;
import cn.mrblackpineapple.trading_website.dto.UserRegisterUpdate;
import cn.mrblackpineapple.trading_website.dto.UserRequest;
import cn.mrblackpineapple.trading_website.dto.UserUpdate;
import cn.mrblackpineapple.trading_website.mapper.UserMapper;
import cn.mrblackpineapple.trading_website.model.User;
import cn.mrblackpineapple.trading_website.service.UserService;
import cn.mrblackpineapple.trading_website.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Result<String> register(UserRegisterUpdate userRegisterUpdate) {
        User entity = userMapper.getByActive(
                User.builder().userAccount(userRegisterUpdate.getUserAccount()).build()
        );

        if (Objects.nonNull(entity)) {
            return ApiResult.error("Account not available");
        }
        User saveEntity = User.builder()
                .userRole(RoleEnum.USER.getRole())
                .userName(userRegisterUpdate.getUserName())
                .userAccount(userRegisterUpdate.getUserAccount())
                .userAvatar(userRegisterUpdate.getUserAvatar())
                .userPwd(userRegisterUpdate.getUserPwd())
                .userEmail(userRegisterUpdate.getUserEmail())
                .createTime(LocalDateTime.now())
                .isLogin(LoginStatusEnum.USE.getFlag())
                .isWord(WordStatusEnum.USE.getFlag()).build();
        userMapper.insert(saveEntity);
        return ApiResult.success("Registration successful");
    }

    @Override
    public Result<Object> login(UserLoginUpdate userLoginUpdate) {
        User user = userMapper.getByActive(
                User.builder().userAccount(userLoginUpdate.getUserAccount()).build()
        );

        if (!Objects.nonNull(user)) {
            return ApiResult.error("Account does not exist");
        }
        if (!Objects.equals(userLoginUpdate.getUserPwd(), user.getUserPwd())) {
            return ApiResult.error("Password error");
        }
        if (user.getIsLogin()) {
            return ApiResult.error("Login status exception");
        }
        String token = JwtUtil.toToken(user.getId(), user.getUserRole());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("role", user.getUserRole());
        User userEntity = new User();
        userEntity.setId(user.getId());
        userEntity.setLastLoginTime(LocalDateTime.now());
        userMapper.update(userEntity);
        return ApiResult.success("Login successful", map);
    }

    @Override
    public Result<UserVO> auth() {
        Integer userId = LocalThreadHolder.getUserId();
        User queryEntity = User.builder().id(userId).build();
        User user = userMapper.getByActive(queryEntity);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ApiResult.success(userVO);
    }

    @Override
    public Result<List<User>> query(UserRequest userRequest) {
        List<User> users = userMapper.query(userRequest);
        Integer count = userMapper.queryCount(userRequest);
        return PageResult.success(users, count);
    }

    @Override
    public Result<String> update(UserUpdate userUpdate) {
        User updateEntity = User.builder().id(LocalThreadHolder.getUserId()).build();
        BeanUtils.copyProperties(userUpdate, updateEntity);
        userMapper.update(updateEntity);
        return ApiResult.success();
    }


    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        userMapper.batchDelete(ids);
        return ApiResult.success();
    }

    @Override
    public Result<String> updatePwd(Map<String, String> map) {
        String oldPwd = map.get("oldPwd");
        String newPwd = map.get("newPwd");
        String againPwd = map.get("againPwd");
        if (Objects.isNull(oldPwd)) {
            return ApiResult.error("Original password cannot be empty");
        }
        if (Objects.isNull(newPwd)) {
            return ApiResult.error("Please enter new password");
        }
        if (Objects.isNull(againPwd)) {
            return ApiResult.error("Please confirm the password");
        }
        if (!newPwd.equals(againPwd)) {
            return ApiResult.error("Passwords do not match");
        }
        User user = userMapper.getByActive(
                User.builder().id(LocalThreadHolder.getUserId()).build()
        );
        if (!user.getUserPwd().equals(oldPwd)) {
            return ApiResult.error("Original password validation failed");
        }
        user.setUserPwd(newPwd);
        userMapper.update(user);
        return ApiResult.success();
    }

    @Override
    public Result<UserVO> getById(Integer id) {
        User user = userMapper.getByActive(User.builder().id(id).build());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ApiResult.success(userVO);
    }

    @Override
    public Result<String> insert(UserRegisterUpdate userRegisterUpdate) {
        return register(userRegisterUpdate);
    }

    @Override
    public Result<String> backUpdate(User user) {
        userMapper.update(user);
        return ApiResult.success();
    }

}