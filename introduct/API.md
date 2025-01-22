## API

### 1. User Model

#### 1.1 register

```apl
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

**Password Encryption**

```java
newUser.setPassword(passwordEncoder.encode(password)) // Encrypt password
```

BCryptPasswordEncoder is a commonly used encryption algorithm that generates an encrypted string with a salt value, which means that even if multiple users use the same password, the encryption result will be different.

**Specific process:**

- **User registration:**
  The user provides a password, and the application encrypts the password using BCryptPasswordEncoder and stores the encrypted password in the database.
- **User login:**
  When the user logs in, the application compares the entered password with the encrypted password stored in the database. If the match is successful, a JWT Token will be generated.
  When generating JWT tokens, use the private key in the. jks file to sign the token, ensuring the integrity and tamper resistance of the token data.
- **Verify JWT Token:**
  In subsequent requests, the client will carry the JWT Token, and the application will verify the signature of the Token using the public key in the. jks file to ensure the validity and security of the request.





#### 1.2 login

```apl
[post] http://localhost:8080/users/login
```

```java
public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
    return userService.login(loginFormDTO);
}
```

