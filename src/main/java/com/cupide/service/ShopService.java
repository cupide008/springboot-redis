package com.cupide.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cupide.dto.Result;
import com.cupide.dto.ShopDto;
import com.cupide.entity.Shop;

public interface ShopService extends IService<Shop> {

    Result sendCode(String phone);
    Result queryList();
    Result updateNum(ShopDto shopDto);
}
