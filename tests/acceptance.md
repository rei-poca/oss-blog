# 测试记录（acceptance）

本项目自动化测试使用 JUnit 5 + MockMvc + Spring Security Test，测试环境使用内存 H2
（`jdbc:h2:mem:blogtest`），每次运行 `create-drop` 保证可重复。核心主流程、权限边界与自主功能
均有自动化用例；界面（响应式/键盘）与数据恢复采用手动可重复步骤验证。

## 自动化测试结果

执行命令：`.\gradlew.bat test`

结果：**21 个用例全部通过**（BUILD SUCCESSFUL）。

| 编号 | 测试方法 | 类型 | 前置条件 | 步骤 | 期望 | 实际 |
| --- | --- | --- | --- | --- | --- | --- |
| F1 | registerCreatesMemberAccount | 功能 | 未注册用户 alice | POST /register | 302 → /login，用户已创建 | 通过 |
| F2 | registerRejectsDuplicateUsername | 功能 | 已存在 admin | POST /register 重名 | 返回 register，提示「用户名已被使用」 | 通过 |
| F3 | registerRejectsMismatchedPassword | 功能 | 两次密码不同 | POST /register | 返回 register，不创建用户 | 通过 |
| F4 | loginWithValidCredentialsSucceeds | 功能 | admin/admin123 | POST /login | 302 → / | 通过 |
| F5 | loginWithInvalidCredentialsFails | 功能 | 错误密码 | POST /login | 302 → /login?error | 通过 |
| F6 | loginPageIsAccessible | 功能 | 匿名 | GET /login | 200，login 视图 | 通过 |
| F7 | homePageListsPublishedPosts | 功能 | 种子数据 | GET / | 200，含《Spring Boot 快速入门》 | 通过 |
| F8 | tagPageFiltersPostsByTag | 功能 | 标签 java | GET /tag/java | 200，含对应文章 | 通过 |
| F9 | searchHitsChineseKeyword | 功能 | 关键词「微服务」 | GET /search?q=微服务 | 200，命中并高亮 | 通过 |
| F10 | searchNoResultShowsHint | 功能 | 不存在关键词 | GET /search?q=… | 200，提示「没有找到」 | 通过 |
| F11 | adminCanCreatePost | 功能 | 管理员登录 | POST /admin/posts/new | 302 → /admin，文章数 +1 | 通过 |
| F12 | adminCannotCreatePostWithEmptyTitle | 功能 | 空标题 | POST /admin/posts/new | 返回表单，提示「标题不能为空」 | 通过 |
| F13 | memberCanComment | 功能 | 会员登录 | POST /post/…/comment | 302 → 文章页，评论 +1 | 通过 |
| P1 | anonymousCannotAccessAdmin | 权限 | 匿名 | GET /admin | 302 → /login | 通过 |
| P2 | memberCannotAccessAdmin | 权限 | 会员 | GET /admin | 403 | 通过 |
| P3 | adminCanAccessAdmin | 权限 | 管理员 | GET /admin | 200，dashboard 视图 | 通过 |
| P4 | anonymousCannotComment | 权限 | 匿名 | POST /post/…/comment | 302 → /login | 通过 |
| S1 | readingMinutesIsEstimatedFromContentLength | 自主功能 | 纯函数 | 调用 BlogUtils.readingMinutes | 时长估算符合规则 | 通过 |
| S2 | relatedPostsShareTagsAndExcludeSelf | 自主功能 | 种子数据 | 调用 PostService.related | 推荐非空、不含自身、共享标签 | 通过 |

## 手动测试用例（界面 / 响应式 / 恢复）

| 编号 | 类型 | 前置条件 | 步骤 | 期望 |
| --- | --- | --- | --- | --- |
| U1 | 主题/界面 | 应用运行 | 桌面浏览器访问首页/详情/搜索 | 无横向溢出，卡片布局正常 |
| U2 | 主题/界面 | 应用运行 | 浏览器缩至 < 520px | 单列布局，导航可点 |
| U3 | 主题/界面 | 应用运行 | 仅键盘（Tab/Enter）操作导航与搜索按钮 | 可聚焦并触发搜索 |
| R1 | 恢复 | 应用已写入文章/评论 | 停止后重新 `bootRun` | 文章、标签、评论仍在（文件型 H2） |
| R2 | 恢复 | 存在 `./data/blogdb` | 备份 `./data` 目录，新目录按 README 冷启动后恢复数据 | 内容可复现 |

## 失败用例与修复记录

开发过程中曾出现以下失败，均已修复并保留对比：

1. **编译失败**：Spring Boot 4 中 `@AutoConfigureMockMvc` 注解不可用 →
   改为 `MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build()` 手动构建。
2. **权限未生效**：手动构建 MockMvc 未挂载 Spring Security 过滤器链，导致匿名/会员可访问受限接口 →
   通过 `.apply(springSecurity())` 修复。
3. **模板渲染异常**：Thymeleaf 3.1 + Spring 6 禁止模板内 `T()` 静态调用 →
   改为控制器预处理高亮与阅读时长。
4. **懒加载异常**：相关推荐在无事务上下文访问 lazy 集合 →
   为 `PostService.related` 增加 `@Transactional(readOnly = true)`，并让测试在事务内运行。

## 负责人

本实验由学生本人独立完成：设计与实现、测试编写与执行、缺陷修复、文档撰写。
