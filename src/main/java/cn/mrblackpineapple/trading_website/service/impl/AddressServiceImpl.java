package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.common.api.ApiResult;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.common.util.PhoneNumberValidateUtil;
import cn.mrblackpineapple.trading_website.dto.AddressRequest;
import cn.mrblackpineapple.trading_website.mapper.AddressMapper;
import cn.mrblackpineapple.trading_website.model.Address;
import cn.mrblackpineapple.trading_website.service.AddressService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Override
    public Result<String> save(Address address) {
        if (!StringUtils.hasText(address.getContactPerson())) {
            return ApiResult.error("Contact person cannot be empty");
        }
        if (!StringUtils.hasText(address.getContactPhone())) {
            return ApiResult.error("Contact phone cannot be empty");
        }
        if (!StringUtils.hasText(address.getAddressDetail())) {
            return ApiResult.error("Address detail cannot be empty");
        }
        if (!PhoneNumberValidateUtil.isValidChinesePhoneNumber(address.getContactPhone())) {
            return ApiResult.error("Contact phone number is invalid");
        }
        address.setUserId(LocalThreadHolder.getUserId());
        dealDefaultAddress(address);
        addressMapper.save(address);
        return ApiResult.success("Shipping address added successfully");
    }

    @Override
    public Result<String> update(Address address) {
        if (!StringUtils.hasText(address.getContactPerson())) {
            return ApiResult.error("Contact person cannot be empty");
        }
        if (!StringUtils.hasText(address.getContactPhone())) {
            return ApiResult.error("Contact phone cannot be empty");
        }
        if (!StringUtils.hasText(address.getAddressDetail())) {
            return ApiResult.error("Address detail cannot be empty");
        }
        if (!PhoneNumberValidateUtil.isValidChinesePhoneNumber(address.getContactPhone())) {
            return ApiResult.error("Contact phone number is invalid");
        }
        dealDefaultAddress(address);
        addressMapper.update(address);
        return ApiResult.success("Shipping address updated successfully");
    }

    private void dealDefaultAddress(Address address) {
        if (address.getIsDefault()) {
            AddressRequest addressRequest = new AddressRequest();
            addressRequest.setIsDefault(true);
            addressRequest.setUserId(LocalThreadHolder.getUserId());
            List<Address> addressList = addressMapper.query(addressRequest);
            if (!addressList.isEmpty()) {
                Address addressSave = addressList.get(0);
                addressSave.setIsDefault(false);
                addressMapper.update(addressSave);
            }
        }
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        addressMapper.batchDelete(ids);
        return ApiResult.success("Shipping address deleted successfully");
    }

    @Override
    public Result<List<Address>> query(AddressRequest addressRequest) {
        int totalCount = addressMapper.queryCount(addressRequest);
        List<Address> addressList = addressMapper.query(addressRequest);
        return ApiResult.success(addressList, totalCount);
    }

    @Override
    public Result<String> isDefault(Address address) {
        dealDefaultAddress(address);
        addressMapper.update(address);
        return ApiResult.success("Set as default address successfully");
    }
}