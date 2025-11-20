package cn.mrblackpineapple.trading_website.mapper;

import cn.mrblackpineapple.trading_website.dto.AddressRequest;
import cn.mrblackpineapple.trading_website.model.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {

    int save(Address address);

    List<Address> query(AddressRequest addressRequest);

    int queryCount(AddressRequest addressRequest);

    int update(Address address);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

}