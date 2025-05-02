package com.cc.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.common.exception.BadRequestException;
import com.cc.common.utils.BeanUtils;
import com.cc.common.utils.CollUtils;
import com.cc.common.utils.UserContext;
import com.cc.user.domain.dto.AddressDTO;
import com.cc.user.domain.po.Address;
import com.cc.user.mapper.AddressMapper;
import com.cc.user.service.IAddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    @Override
    public AddressDTO findAddressById(Long addressId) {
        // 1.根据id查询
        Address address = getById(addressId);
        // 2.判断当前用户
        Long userId = UserContext.getUser();
        if(!address.getUserId().equals(userId)){
            throw new BadRequestException("The address does not belong to the currently logged-in user");
        }
        return BeanUtils.copyBean(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> findMyAddresses() {
        // 1.查询列表
        List<Address> list = query().eq("user_id", UserContext.getUser()).list();
        // 2.判空
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        // 3.转vo
        return BeanUtils.copyList(list, AddressDTO.class);
    }

    @Override
    public Boolean addAddress(AddressDTO addressDTO) {
        Long Id = UserContext.getUser();
        Address address = BeanUtils.copyBean(addressDTO, Address.class);
        address.setUserId(Id);
        return save(address);
    }

    @Override
    public Boolean deleteAddress(Long addressId) {
        return removeById(addressId);
    }
}