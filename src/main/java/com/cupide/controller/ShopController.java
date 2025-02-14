package com.cupide.controller;


import com.cupide.dto.Result;
import com.cupide.dto.ShopDto;
import com.cupide.entity.Shop;
import com.cupide.service.ShopService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private ShopService shopService;


    // 发送验证码
    @GetMapping("code")
    public Result sendCode(String phone){
        return shopService.sendCode(phone);
    }

    @GetMapping("list")
    public Result list(){
        return shopService.queryList();
    }

    @PostMapping("/update")
    public Result update(@RequestBody ShopDto shopDto){
        return shopService.updateNum(shopDto);
    }
}
