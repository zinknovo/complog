# 重新导入项目步骤

## 方法 1：完全重新导入（推荐）

1. **关闭当前项目**
   - `File` → `Close Project`

2. **删除旧的 IDE 配置**（可选，如果方法 1 不行）
   - 关闭 IntelliJ IDEA
   - 删除 `.idea` 目录（已部分清理）
   - 重新打开 IntelliJ IDEA

3. **重新导入项目**
   - 在欢迎界面选择 `Open` 或 `Import Project`
   - 选择项目目录：`/Users/Z1nk/Desktop/proj/clock-in`
   - **重要**：选择 `Import project from external model` → 选择 `Maven`
   - 点击 `Next` → `Next` → `Next` → `Finish`

4. **等待 Maven 导入完成**
   - 右下角会显示 "Importing Maven projects..."
   - 等待所有依赖下载完成

## 方法 2：刷新 Maven 项目（如果项目已打开）

1. **右键点击 `pom.xml`**
   - 选择 `Maven` → `Reload project`

2. **如果还不行，清除缓存**
   - `File` → `Invalidate Caches...`
   - 勾选所有选项
   - 点击 `Invalidate and Restart`

## 方法 3：检查 Maven 设置

1. **打开 Maven 设置**
   - `File` → `Settings` (Windows/Linux) 或 `Preferences` (Mac)
   - `Build, Execution, Deployment` → `Build Tools` → `Maven`

2. **确认设置**
   - Maven home path: 应该是你的 Maven 安装路径
   - User settings file: 应该是 `~/.m2/settings.xml` 或默认
   - Local repository: 应该是 `~/.m2/repository`

3. **检查 JDK 设置**
   - `File` → `Project Structure` → `Project`
   - Project SDK: 应该选择 Java 11
   - Project language level: 应该选择 11

## 验证

导入完成后，检查：
- `DemoApplication.java` 中的红色错误应该消失
- Maven 工具窗口中的依赖应该没有红色下划线
- 可以正常运行 `main` 方法

