package com.cc.user.controller;

import com.cc.common.exception.BadRequestException;
import com.cc.common.utils.BeanUtils;
import com.cc.common.utils.CollUtils;
import com.cc.common.utils.UserContext;
import com.cc.user.domain.dto.AddressDTO;
import com.cc.user.domain.po.Address;
import com.cc.user.service.IAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Api(tags = "收货地址管理接口")
public class AddressController {

    private final IAddressService addressService;

    @ApiOperation("根据id查询地址")
    @GetMapping("{addressId}")
    public AddressDTO findAddressById(@ApiParam("地址id") @PathVariable("addressId") Long id) {
        return addressService.findAddressById(id);
    }
    @ApiOperation("查询当前用户地址列表")
    @GetMapping
    public List<AddressDTO> findMyAddresses() {
        return addressService.findMyAddresses();
    }

    @ApiOperation("添加地址")
    @PostMapping("/add")
    public Boolean addAddress(@RequestBody AddressDTO addressDTO) {
        return addressService.addAddress(addressDTO);
    }

    @ApiOperation("删除地址")
    @DeleteMapping("{addressId}")
    public Boolean deleteAddress(@ApiParam("地址id") @PathVariable("addressId") Long addressId) {
        return addressService.deleteAddress(addressId);
    }
}