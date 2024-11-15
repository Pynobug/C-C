package com.cc.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cc.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * username
     */
    private String username;

    /**
     * password,jwt
     */
    private String password;

    /**
     * register_phone
     */
    private String phone;

    /**
     * creat time
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * Status (normal 1, frozen 2)
     */
    private UserStatus status;

    /**
     * balance
     */
    private Integer balance;
}
