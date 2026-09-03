# NOTICE — 第三方资源与许可证

本项目（oss-blog）为课程实验交付物，源代码为本人独立实现，未直接复制上游项目或第三方源码。
以下是本项目所依赖/参考的第三方资源及其许可证说明。

## 依赖库（通过 Gradle 声明，未修改其源码）

| 依赖 | 版本 | 许可证 |
| --- | --- | --- |
| Spring Boot | 4.1.1 | Apache-2.0 |
| Spring Security | 随 Boot 管理 | Apache-2.0 |
| Spring Data JPA | 随 Boot 管理 | Apache-2.0 |
| Thymeleaf | 随 Boot 管理 | Apache-2.0 |
| H2 Database Engine | 2.4.240（运行时解析） | EPL 1.0 / MPL 2.0 |
| Jakarta Validation | 随 Boot 管理 | Apache-2.0 |
| JUnit 5 | 随 Boot 测试管理 | EPL 2.0 |
| AssertJ / Hamcrest / Mockito | 随 Boot 测试管理 | 各自开源许可证 |

以上依赖均由 Spring Boot BOM 统一版本管理，具体版本见 Gradle 依赖解析结果。

## 参考项目（业务规范，非代码依赖）

- RealWorld（realworld-apps/realworld）：https://github.com/realworld-apps/realworld
  - 用途：博客业务规范、API 规范与 E2E 测试思路的来源。
  - 该仓库未给出单一 SPDX 许可证，使用前需逐项核对各资产许可证。
  - 本项目仅参考其接口/业务约定，未克隆、复制其代码。

## 图片 / 字体 / 数据

- 本项目未使用外部图片、CDN、字体或第三方数据集；界面为纯 CSS 实现，数据为程序内置演示数据。

## 许可证兼容性说明

本项目不选择与上游冲突的许可证。如后续对外发布，将遵循所依赖库的许可证要求，
并在网络部署场景中保留 AGPL 等强 Copyleft 项目的源码提供义务说明。
