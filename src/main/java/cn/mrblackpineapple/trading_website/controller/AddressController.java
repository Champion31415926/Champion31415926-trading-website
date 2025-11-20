package cn.mrblackpineapple.trading_website.controller;

import cn.mrblackpineapple.trading_website.aspect.Pager;
import cn.mrblackpineapple.trading_website.aspect.Protector;
import cn.mrblackpineapple.trading_website.common.api.Result;
import cn.mrblackpineapple.trading_website.common.context.LocalThreadHolder;
import cn.mrblackpineapple.trading_website.dto.AddressRequest;
import cn.mrblackpineapple.trading_website.model.Address;
import cn.mrblackpineapple.trading_website.service.AddressService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    @Protector
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody Address address) {
        return addressService.save(address);
    }

    @Protector
    @PutMapping(value = "/update")
    public Result<String> update(@RequestBody Address address) {
        return addressService.update(address);
    }

    @Protector
    @PutMapping(value = "/isDefault")
    public Result<String> isDefault(@RequestBody Address address) {
        return addressService.isDefault(address);
    }

    @Protector
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return addressService.batchDelete(ids);
    }

    @Protector
    @Pager
    @PostMapping(value = "/query")
    public Result<List<Address>> query(@RequestBody AddressRequest addressRequest) {
        return addressService.query(addressRequest);
    }

    @Protector
    @Pager
    @PostMapping(value = "/queryUser")
    public Result<List<Address>> queryUser(@RequestBody AddressRequest addressRequest) {
        addressRequest.setUserId(LocalThreadHolder.getUserId());
        return addressService.query(addressRequest);
    }

}