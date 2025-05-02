package com.cc.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.user.domain.dto.AddressDTO;
import com.cc.user.domain.po.Address;

import java.util.List;

public interface IAddressService extends IService<Address> {
    AddressDTO findAddressById(Long addressId);

    List<AddressDTO> findMyAddresses();

    Boolean addAddress(AddressDTO addressDTO);

    Boolean deleteAddress(Long addressId);
}