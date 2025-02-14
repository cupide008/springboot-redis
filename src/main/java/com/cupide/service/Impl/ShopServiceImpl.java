package com.cupide.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupide.dto.Result;
import com.cupide.dto.ShopDto;
import com.cupide.entity.Shop;
import com.cupide.mapper.ShopMapper;
import com.cupide.service.ShopService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Result sendCode(String phone) {

        // 1.生成验证码
        String code = RandomUtil.randomNumbers(6);

        log.info("向{}发送验证码：{}",phone, code);
        // 3.保存验证码到redis
        stringRedisTemplate.opsForValue().set("code:"+phone, code,5, TimeUnit.MINUTES);

        return Result.ok();
    }

    @Override
    public Result queryList() {

        String key = "shop:";
        // 先从redis中查询数据
        String shops = stringRedisTemplate.opsForValue().get(key);

        // 如果redis中有数据，直接返回
        if (StrUtil.isNotBlank(shops)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Shop> typeList = objectMapper
                        // 将 JSON 字符串（content）反序列化为指定类型的 Java 对象（valueType）
                        .readValue(shops,
                                objectMapper
                                        // 构建一个集合类型，用于反序列化为集合对象
                                        .getTypeFactory()
                                        .constructCollectionType(List.class, Shop.class));
                return Result.ok(typeList);
            } catch (JsonProcessingException e) {
                e.fillInStackTrace();
                return Result.fail("商铺类型解析失败");
            }
        }

        // 如果redis中没有数据，从数据库中查询
        List<Shop> shopList = list();
        if (shopList.isEmpty()) {
            return Result.fail("没有数据");
        }
        // 将数据存入redis
        try {
            stringRedisTemplate.opsForValue().set(key,
                    // 将传入的对象（value）转换为 JSON 格式的字符串
                    new ObjectMapper().writeValueAsString(shopList));
        } catch (JsonProcessingException e) {
            e.fillInStackTrace();
            return Result.fail("存入redis失败");
        }

        return Result.ok(shopList);
    }

    @Override
    public Result updateNum(ShopDto shopDto) {

        String code = stringRedisTemplate.opsForValue().get("code:" + shopDto.getPhone());

        log.info("code:{}", code);
        if (StrUtil.isBlank(code)){
            return Result.fail("验证码已过期");
        }
        if (code.equals(shopDto.getCode())) {
            Shop shop = BeanUtil.copyProperties(shopDto, Shop.class);
            boolean b = updateById(shop);

            List<Shop> shopList = list();

            try {
                stringRedisTemplate.opsForValue().set("shop:",
                        // 将传入的对象（value）转换为 JSON 格式的字符串
                        new ObjectMapper().writeValueAsString(shopList));
            } catch (JsonProcessingException e) {
                e.fillInStackTrace();
                return Result.fail("存入redis失败");
            }


            if (!b) {
                return Result.fail("更新失败");
            }
            return Result.ok();
        }

        return Result.fail("验证码错误");

    }
}
