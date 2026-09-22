# 开发规范与技术栈对齐说明

核对日期：2026-09-21。核对仓库：`chaoyue-marketing-server`，只读检查时 HEAD 为 `c42ae99`。本文记录 AGENTS.md 的来源和适用边界，不作为依赖升级、业务开发或生产验收结果。

## 1. 本次产物

| 文件 | 用途 |
|---|---|
| [根 AGENTS.md](../../AGENTS.md) | Agent 开工顺序、技术基线、后端规范、租户安全、审核、版本、测试与交付 |
| [前端 AGENTS.md](../../chaoyue-ui/chaoyue-ui-admin-vue3/AGENTS.md) | Vue 3/TypeScript、API/组件、交互安全、视觉和前端验证 |
| 本文件 | 本地实查、官方来源、框架差异、未落地能力与验证边界 |

保留两份 CLAUDE.md 与前端 DESIGN.md 原文；本轮不重写其产品策略、不升级依赖、不补建脚手架。根规范继承当前 CLAUDE.md 的自审/双审、版本与幂等规则；设计中的“待产品确认”仍然保持待确认，不由编程规范代替产品决议。

## 2. 本地技术栈核对

| 项目 | 本地依据 | 结论 |
|---|---|---|
| Java 编译级别 | `pom.xml` 的 java.version/source/target | 17；架构中的 JDK 21 运行目标不能视作已升级语言基线 |
| Spring Boot | 根 POM、`chaoyue-dependencies/pom.xml` | 3.5.15；框架依赖跟随 BOM |
| MyBatis Plus | dependencies POM | 3.5.17，与旧架构表的 3.5.16 不同 |
| Spring AI | `chaoyue-module-ai/pom.xml` | 1.1.8，Alibaba 1.1.2.2；不默认为 LangChain4j 项目 |
| 前端 | 前端 README、CLAUDE、DESIGN、少量 src 文件 | Vue3/Element Plus 方向明确；package.json、pnpm 锁文件、tsconfig/lint 配置尚未到位 |
| marketing 模块 | 根模块声明、目录、BASELINE | 尚未创建；api/biz 分层是规划而非已有工程 |
| Flyway | 仓库 POM 检索 | 未发现接入声明；需设计并实施接入，不能把 SQL 文件保存视为已执行迁移 |
| 测试 | system 测试目录与 test starter | 已有 JUnit 5、Mockito、BaseDbUnitTest 等代码设施；本轮未运行测试 |

不写死尚不存在的 Node/pnpm/Vue/Vite/TypeScript 精确版本。前端工程落地时应记录提交号、依赖清单、锁文件、包管理器与实际脚本，再更新规范。

## 3. 芋道与若依的适用差异

下表以本地代码为实现依据，若依文档只用于对照。不得将两个框架的同名概念视为可直接替换。

| 方面 | 本仓库采用 | 不直接照搬的若依示例 |
|---|---|---|
| 返回体 | `CommonResult<T>`，code/data/msg | AjaxResult |
| 分页 | PageParam/PageResult，pageNo/pageSize，list/total | PageHelper/startPage，pageNum，rows/total |
| 权限表达式 | `@ss.hasPermission(...)` | `@ss.hasPermi(...)` |
| 持久层 | BaseMapperX、LambdaQueryWrapperX、MyBatis Plus | 另一套 Mapper/分页基础设施 |
| API 前缀 | 根据 controller.admin/app 自动添加 | 不按前端导航 /admin 或 /tenant 直接推断 |
| Vue HTTP 封装 | `@/config/axios`，具体类型与解包以接入工程为准 | `@/utils/request.js` 的旧版实现 |
| 前端状态与 UI | Vue3/TypeScript/Pinia/Element Plus | Vue2/Vuex/Element UI |
| 多租户与审核 | 项目业务归属、数据范围、平台角色、版本闸门共同判定 | 只依赖按钮权限或前端角色隐藏 |

本地核对的代表性文件：

- `chaoyue-module-system/src/main/java/cn/iocoder/yudao/module/system/controller/admin/notice/NoticeController.java`
- `chaoyue-module-system/src/main/java/cn/iocoder/yudao/module/system/service/notice/NoticeServiceImpl.java`
- `chaoyue-module-system/src/test/java/cn/iocoder/yudao/module/system/service/notice/NoticeServiceImplTest.java`
- `chaoyue-framework/chaoyue-common/src/main/java/cn/iocoder/yudao/framework/common/pojo/CommonResult.java`
- `chaoyue-framework/chaoyue-spring-boot-starter-web/src/main/java/cn/iocoder/yudao/framework/web/core/handler/GlobalExceptionHandler.java`
- `chaoyue-framework/chaoyue-spring-boot-starter-biz-tenant/src/main/java/cn/iocoder/yudao/framework/tenant/core/db/TenantBaseDO.java`
- `chaoyue-module-infra/src/main/resources/codegen/vue3/api/api.ts.vm`
- `chaoyue-module-infra/src/main/resources/codegen/vue3/views/form.vue.vm`

生成器中可能仍出现宽泛 any 或通用 CRUD 逻辑，规范要求生成后补齐类型与业务约束，不声称这些质量约束已在基线全仓实施。

## 4. 官方来源与采用范围

以下均在本轮直接读取或经 Context7 查询，提取的规则结合本地代码适配，不整段搬用。

| 来源 | 采用内容 | 边界 |
|---|---|---|
| [芋道：新建模块](https://doc.iocoder.cn/module-new/) | 模块接入、Controller admin/app 自动前缀、CommonResult | 官方坐标 yudao 需适配本地 chaoyue，不照搬 demo 的无权限接口 |
| [芋道：Vue3 开发规范](https://doc.iocoder.cn/vue3/dev-spec/) | api/views/components/styles 分工、统一 axios、分页响应 | 文档示例不代表当前前端已有完整依赖工程 |
| [若依：后台手册](https://doc.ruoyi.vip/ruoyi-vue/document/htsc.html) | 服务端权限、事务、校验、分页等原则对照 | 不采用其不同的返回体、分页和权限方法名称 |
| [若依：前端手册](https://doc.ruoyi.vip/ruoyi-vue/document/qdsc.html) | API 集中管理、局部组件、scoped、前端权限不能替代后端 | 旧版 Vue/Element 示例不适用于本项目新页面 |
| [Java：异常与资源处理](https://dev.java/learn/exceptions/catching-handling/) | 显式处理异常、资源生命周期、try-with-resources | 页面可能出现新 JDK 示例；仅采用 Java 17 可用能力 |
| [Java：JDK 内部 API 封装](https://dev.java/learn/strong-encapsulation-%28of-jdk-internals%29/) | 优先公共 Java API，避免依赖 JDK 内部实现 | 不是本项目新的框架依赖要求 |
| [Spring 6.2：事务注解](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/declarative/annotations.html) | 代理边界、同类自调用不触发事务拦截 | 不把在方法上标注注解视为运行时一定有效 |
| [Spring 6.2：回滚规则](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/declarative/rolling-back.html) | unchecked/checked 默认回滚差异、显式 rollbackFor | 多数据源语义仍需按实际事务管理器验证 |
| [Spring 6.2：Bean Validation](https://docs.spring.io/spring-framework/reference/6.2/core/validation/beanvalidation.html) | Jakarta Bean Validation 集成 | 遵循本地 Boot 3/Jakarta，保留 Java SE 自身的 javax API；不是全量字符串替换 javax |
| [Vue：TypeScript 概览](https://vuejs.org/guide/typescript/overview.html) | SFC TS、vue-tsc；Vite 转译不等于类型检查 | 具体命令需依赖 package.json/tsconfig |
| [Vue：Composition API 类型](https://vuejs.org/guide/typescript/composition-api) | 类型化 props/emits 与 Composition API | 宏语法按实际 Vue 版本判断 |
| [Vue：侦听器](https://vuejs.org/guide/essentials/watchers) | 旧请求清理、watch cleanup、onWatcherCleanup 3.5+ | onWatcherCleanup 在 await 前同步注册；不要无条件套用新 API |
| [Vue：内置指令](https://vuejs.org/api/built-in-directives) | v-html 渲染不可信 HTML 的风险 | 项目采用服务端净化与 sandbox 预览的进一步约束 |

Java 命名、精确金额、分页上限、跨租户测试、日志脱敏等条目包含本项目工程约束；并非宣称以上每条官方资料都逐字规定了这些要求。

### Context7 查询记录

- 先 resolve `Spring Framework`，选用返回的 `/websites/spring_io_spring-framework_reference_6_2`，避免泛指 current 而误引用 Spring 7。
- 查询 Spring 事务代理、自调用、默认回滚和 Jakarta Validation；结果返回上述 Spring 6.2 官方页面。
- 先 resolve `Vue`，选用 `/websites/vuejs`；查询 TypeScript/Composition API、props/emits、watch 清理、vue-tsc 和 HTML 渲染。
- Vue 结果包含 3.5+ 限制；已在前端规范明确版本前提，没有把 Context7 的最新示例写成本地已支持功能。
- Java 核心异常与资源管理补充使用 dev.java 官方资料；没有把第三方博客当作语言规范。

## 5. 与 CLAUDE.md 对齐及仍需实施的事项

| 项目 | 规范处理 |
|---|---|
| 最新闸门修订 | 继承三个独立判定、版本/哈希、confirmed、规则有效期、默认双审，前端只消费 GateResult |
| 自审默认值 | 记录当前 CLAUDE 的关闭默认；详细设计仍标待产品确认时，不冒充已经正式确认 |
| 审批与正文版本 | 继承 expectedVersionId/冲突拒绝；明确新内容仍需完成必要预检与审核 |
| HTTP 409 | 标明业务错误码和 HTTP 状态不同，现有 CommonResult 异常处理不会因为文字写 409 就自动设置响应状态 |
| Token 与租户 | 区分登录前发现和登录后授权；ADMIN Token/app-api MEMBER 兼容性需受控适配与测试 |
| 文件与凭据 | 要求验证对象归属、下载权限、租户 Key 托管，不视为现有 infra/ai CRUD 已自动满足 |
| Java 包与配置迁移 | 沿用当前 cn.iocoder 包和 yudao 配置，避免只改部分文件破坏扫描与绑定 |
| 规范维护 | 根/前端 AGENTS 与 CLAUDE/视觉正本同步，不复制整套 PRD，引用旧审查结论前重新验证 |

## 6. 本轮验证边界

本轮新增上述三份 Markdown；另在仓库 .gitignore 中仅放行这三个路径，覆盖用户全局 *.md 忽略规则，便于随仓库管理。验证文件落点、相对链接、关键版本/约束、现有正本未被改写，以及真实 Git 根的 diff 检查。未执行 Maven 构建、后端测试、前端安装/类型检查/构建，未接入真实模型、数据库或对象存储。

规范是一份后续开发执行依据，不代表所要求的安全、审核、测试和工程能力已经实现。
