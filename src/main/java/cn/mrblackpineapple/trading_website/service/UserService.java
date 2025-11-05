package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.UserLoginUpdate;
import cn.mrblackpineapple.trading_website.dto.UserRegisterUpdate;
import cn.mrblackpineapple.trading_website.dto.UserRequest;
import cn.mrblackpineapple.trading_website.dto.UserUpdate;
import cn.mrblackpineapple.trading_website.model.User;
import cn.mrblackpineapple.trading_website.vo.UserVO;

import java.util.List;
import java.util.Map;

public interface UserService {
    Result<String> register(UserRegisterUpdate userRegisterUpdate);

    Result<Object> login(UserLoginUpdate userLoginUpdate);

    Result<UserVO> auth();

    Result<List<User>> query(UserRequest userRequest);

    Result<String> update(UserUpdate userUpdate);

    Result<String> batchDelete(List<Integer> ids);

    Result<String> updatePwd(Map<String, String> map);

    Result<UserVO> getById(Integer id);

    Result<String> insert(UserRegisterUpdate userRegisterUpdate);

    Result<String> backUpdate(User user);

}