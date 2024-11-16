package com.cc.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "The registration form entity")
public class RegisterFormDTO {
    @NotNull(message = "Username cannot be empty")
    @ApiModelProperty(value = "Username", required = true)
    private String username;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @ApiModelProperty(value = "Password", required = true)
    private String password;

    @NotNull(message = "Confirm password cannot be empty")
    @ApiModelProperty(value = "Confirm Password", required = true)
    private String confirmPassword;

    @NotNull(message = "Phone number cannot be empty")
    @ApiModelProperty(value = "Phone number", required = true)
    private String phone;
}