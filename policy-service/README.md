# Policy Service - 制度服务

基于 Spring Boot 的制度管理微服务，提供制度 CRUD、修订、审议等功能。

## 功能

- ✅ 制度管理（Policy CRUD）
- ✅ 制度修订（Policy Revision）
- ✅ 制度审议（Policy Review）
- ✅ 版本管理（Version History）
- ✅ 乐观锁（Optimistic Locking）
- ✅ Redis 缓存

## 技术栈

- Java 11
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.2
- Redis
- MySQL 8.0

## 项目结构

```
policy-service/
├── src/main/java/com/example/policy/
│   ├── controller/          # 控制器
│   ├── service/             # 服务层
│   ├── mapper/              # 数据访问层
│   ├── domain/              # 实体类
│   ├── vo/                   # 视图对象
│   ├── config/               # 配置类
│   ├── response/             # 响应类
│   └── pattern/              # 设计模式
└── src/main/resources/
    └── application.yml       # 配置文件
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

### 2. 配置Redis

```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### 3. 运行服务

```bash
cd policy-service
mvn spring-boot:run
```

服务将在 `http://localhost:8082` 启动。

## API 接口

### 制度管理

- `GET /api/policy/list` - 制度列表
- `GET /api/policy/detail/{id}` - 制度详情
- `POST /api/policy/add` - 添加制度
- `PUT /api/policy/edit` - 编辑制度
- `DELETE /api/policy/del/{id}` - 删除制度
- `GET /api/policy/version-history/{policyId}` - 版本历史

### 制度修订

- `POST /api/policy-revision/create` - 创建修订
- `GET /api/policy-revision/detail/{id}` - 修订详情
- `PUT /api/policy-revision/edit/{id}` - 编辑修订
- `POST /api/policy-revision/submit/{id}` - 提交审议
- `GET /api/policy-revision/progress/{id}` - 修订进度

## 注意事项

1. ⚠️ **依赖问题**：当前代码中可能引用了 Department 和 User 实体，这些需要：
   - 通过 API 调用主项目获取
   - 或创建简化的 DTO 类
   - 或暂时注释相关代码

2. ✅ **数据库**：共享主项目的 MySQL 数据库（CompLog）

3. ✅ **Redis**：使用独立的 Redis 实例或共享 Redis

## 下一步

- [ ] 修复 Department/User 依赖问题
- [ ] 添加服务间调用（Feign/RestTemplate）
- [ ] 添加 Kafka 事件发布
- [ ] 集成 Elasticsearch 搜索