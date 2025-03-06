package com.cc.user.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "The login form entity")
public class LoginFormDTO {
    @NotNull(message = "Username cannot be empty")
    @ApiModelProperty(value = "Username", required = true)
    private String username;

    @NotNull(message = "Password cannot be empty")
    @ApiModelProperty(value = "Password", required = true)
    private String password;

    @ApiModelProperty(value = "Remember me or not", required = false)
    private Boolean rememberMe = false;
}
