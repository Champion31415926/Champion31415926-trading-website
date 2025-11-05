package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.UserRequest;
import cn.mrblackpineapple.trading_website.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    int insert(User user);

    List<User> query(UserRequest userRequest);

    int queryCount(UserRequest userRequest);

    int update(User user);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    User getByActive(User user);

}