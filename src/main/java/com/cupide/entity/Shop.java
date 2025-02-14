package com.cupide.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("shop")
public class Shop {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String number;
}
