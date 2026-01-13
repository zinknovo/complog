# Auth Service - 认证服务

基于 Spring Boot 的认证微服务，提供 JWT 认证功能。

## 功能

- ✅ 用户登录（手机号 + 密码）
- ✅ JWT Token 生成和验证
- ✅ Token 认证拦截器
- ✅ 用户信息查询

## 技术栈

- Java 11
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- JWT (jjwt 0.11.5)
- MySQL 8.0

## 项目结构

```
auth-service/
├── src/main/java/com/example/auth/
│   ├── controller/          # 控制器
│   ├── service/             # 服务层
│   ├── mapper/              # 数据访问层
│   ├── model/               # 实体类
│   ├── util/                # 工具类
│   ├── middleware/          # 中间件（拦截器）
│   └── config/               # 配置类
└── src/main/resources/
    └── application.yml      # 配置文件
```

## 快速开始

### 1. 配置数据库

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/CompLog?...
    username: root
    password: your_password
```

### 2. 配置JWT密钥

修改 `application.yml` 中的 `jwt.secret`（至少32个字符）：

```yaml
jwt:
  secret: your-secret-key-change-in-production-min-32-chars
  expire-hours: 24
```

### 3. 添加密码字段（如果数据库没有）

执行SQL脚本添加password字段：

```sql
USE CompLog;
ALTER TABLE `user` ADD COLUMN password VARCHAR(255) COMMENT '密码（MD5加密）' AFTER phone;
```

### 4. 运行服务

```bash
cd auth-service
mvn spring-boot:run
```

服务将在 `http://localhost:8081` 启动。

## API 接口

### 1. 用户登录

**POST** `/api/auth/login`

请求体：
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "deptId": 1,
      "role": "user",
      "status": 1
    }
  }
}
```

### 2. 验证Token

**GET** `/api/auth/verify`

请求头：
```
Authorization: Bearer {token}
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

## 使用说明

### 在其他服务中使用

1. **调用登录接口获取token**
2. **在请求头中携带token**：
   ```
   Authorization: Bearer {token}
   ```

### 密码加密

当前使用 MD5 加密（简化版），生产环境建议使用 BCrypt：

```java
// 使用 BCrypt
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode(password);
```

## 注意事项

1. ⚠️ **JWT Secret**：生产环境必须修改为强密钥
2. ⚠️ **密码加密**：当前使用MD5，建议升级为BCrypt
3. ⚠️ **数据库字段**：确保user表有password字段
4. ✅ **Token过期**：默认24小时，可在配置中修改

## 下一步

- [ ] 添加密码重置功能
- [ ] 添加刷新Token机制
- [ ] 集成Redis缓存用户信息
- [ ] 添加RBAC权限控制
- [ ] 添加多租户支持