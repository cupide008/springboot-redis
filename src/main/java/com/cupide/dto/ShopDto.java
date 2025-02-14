package com.cupide.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
public class ShopDto {
    private Integer id;
    private String name;
    private String number;
    private String phone;
    private String code;
}
