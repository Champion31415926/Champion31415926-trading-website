package cn.mrblackpineapple.trading_website.service;

import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.dto.AddressRequest;
import cn.mrblackpineapple.trading_website.model.Address;

import java.util.List;

public interface AddressService {

    Result<String> save(Address address);

    Result<String> update(Address address);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<Address>> query(AddressRequest addressRequest);

    Result<String> isDefault(Address address);

}