# CompLog - 制度管理系统

微服务架构的制度管理系统。

## 项目结构

```
complog/
├── base-service/      # 基础服务（User、Department 管理）
├── auth-service/      # 认证服务（登录、JWT）
├── policy-service/    # 业务服务（制度管理）
├── docs/              # 文档
└── scripts/           # SQL 脚本
```

## 服务说明

### base-service
- **端口**: 8080
- **功能**: User 管理、Department 管理
- **技术**: Spring Boot + MyBatis-Plus + Redis + Kafka

### auth-service
- **端口**: 8081
- **功能**: 用户认证、JWT 生成/验证
- **技术**: Spring Boot + MyBatis-Plus + JWT

### policy-service
- **端口**: 8082
- **功能**: Policy 管理、Revision、Review
- **技术**: Spring Boot + MyBatis-Plus + Redis + Kafka

## 快速开始

### 1. 启动基础服务
```bash
cd base-service
./mvnw spring-boot:run
```

### 2. 启动认证服务
```bash
cd auth-service
mvn spring-boot:run
```

### 3. 启动业务服务
```bash
cd policy-service
mvn spring-boot:run
```

## 数据库

所有服务共享 MySQL 数据库：**CompLog**

## 消息队列

使用 Kafka 进行异步通信：
- `policy-events` - 制度事件
- `user-events` - 用户事件
- `department-events` - 部门事件

## 详细文档

- [架构说明](ARCHITECTURE.md)
- [Kafka 配置](docs/KAFKA_SETUP.md)
- [升级方案](docs/UPGRADE_PLAN.md)
