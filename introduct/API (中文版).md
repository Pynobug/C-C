## API (中文版)

### 1. 用户模块

#### 1.1 注册

```http
[post] http://localhost:8080/users/register
```

```java
public Boolean register(@RequestBody @Validated RegisterFormDTO registerFormDTO) {
	return userService.register(registerFormDTO);
}
```

```java
public Boolean register(RegisterFormDTO registerFormDTO) {
    String username = registerFormDTO.getUsername();
    String password = registerFormDTO.getPassword();
    String confirmPassword = registerFormDTO.getConfirmPassword();
    String phone = registerFormDTO.getPhone();

    // 1. Check if passwords match
    if (!password.equals(confirmPassword)) {
        throw new BadRequestException("Passwords do not match");
    }

    // 2. Check if username already exists
    User existingUser = lambdaQuery().eq(User::getUsername, username).one();
    if (Objects.nonNull(existingUser)) {
        throw new BadRequestException("Username already exists");
    }

    // 3. Create new user
    User newUser = new User();
    newUser.setUsername(username)
        .setPassword(passwordEncoder.encode(password)) // Encrypt password
        .setPhone(phone)
        .setStatus(UserStatus.NORMAL) // Set default status
        .setBalance(0) // Set initial balance
        .setCreateTime(LocalDateTime.now())
        .setUpdateTime(LocalDateTime.now());

    // 4. Save user to the database
    boolean isSaved = save(newUser);
    return isSaved;
}
```

密码加密

```java
newUser.setPassword(passwordEncoder.encode(password)) // Encrypt password
```

BCryptPasswordEncoder是一种常用的加密算法，它生成一个带有盐值的加密字符串，这意味着即使多个用户使用相同的密码，加密结果也会不同。

**具体流程：**

- **用户注册：**
  用户提供密码，应用程序使用BCryptPasswordEncoder对密码进行加密，并将加密后的密码存储在数据库中。
- **用户登录：**
  当用户登录时，应用程序会将输入的密码与数据库中存储的加密密码进行比较。如果匹配成功，将生成JWT令牌。
  生成JWT令牌时，请使用中的私钥。jks文件对令牌进行签名，确保令牌数据的完整性和防篡改性。
- **验证 JWT 令牌：**
  在后续请求中，客户端将携带JWT令牌，应用程序将使用中的公钥验证令牌的签名。jks文件，以确保请求的有效性和安全性。





#### 1.2 登录

```http
[post] http://localhost:8080/users/login
```

```java
public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
    return userService.login(loginFormDTO);
}
```









### 2. 商品模块

#### 2.1 搜索

```http
[post] http://localhost:8080/search/list
```

```java
public PageDTO<ItemDoc> search(ItemPageQuery query) throws IOException{
    return itemService.Search(query);
}
```

