package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.UserLoginUpdate;
import cn.mrblackpineapple.trading_website.dto.UserRegisterUpdate;
import cn.mrblackpineapple.trading_website.dto.UserRequest;
import cn.mrblackpineapple.trading_website.dto.UserUpdate;
import cn.mrblackpineapple.trading_website.model.User;
import cn.mrblackpineapple.trading_website.service.UserService;
import cn.mrblackpineapple.trading_website.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping(value = "/login")
    public Result<Object> login(@RequestBody UserLoginUpdate userLoginUpdate) {
        return userService.login(userLoginUpdate);
    }

    @Protector
    @GetMapping(value = "/auth")
    public Result<UserVO> auth() {
        return userService.auth();
    }


    @Protector
    @GetMapping(value = "/getById/{id}")
    public Result<UserVO> getById(@PathVariable Integer id) {
        return userService.getById(id);
    }


    @PostMapping(value = "/register")
    public Result<String> register(@RequestBody UserRegisterUpdate userRegisterUpdate) {
        return userService.register(userRegisterUpdate);
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/insert")
    public Result<String> insert(@RequestBody UserRegisterUpdate userRegisterUpdate) {
        return userService.insert(userRegisterUpdate);
    }

    @Protector
    @PutMapping(value = "/update")
    public Result<String> update(@RequestBody UserUpdate userUpdate) {
        return userService.update(userUpdate);
    }

    @Protector(role = "Administrator")
    @PutMapping(value = "/backUpdate")
    public Result<String> backUpdate(@RequestBody User user) {
        return userService.backUpdate(user);
    }

    @PutMapping(value = "/updatePwd")
    public Result<String> updatePwd(@RequestBody Map<String, String> map) {
        return userService.updatePwd(map);
    }

    @Protector(role = "Administrator")
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return userService.batchDelete(ids);
    }

    @Pager
    @Protector(role = "Administrator")
    @PostMapping(value = "/query")
    public Result<List<User>> query(@RequestBody UserRequest userRequest) {
        return userService.query(userRequest);
    }
}