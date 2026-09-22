# 智造营销 SaaS · Agent 开发规范

适用范围：本仓库及全部子目录。前端另读 `chaoyue-ui/chaoyue-ui-admin-vue3/AGENTS.md`。本文为 Codex 等编程 Agent 的执行入口；使用标准文件名 `AGENTS.md`，不建立 `agent.md`/`agents.md` 等重复正本。

## 1. 开工顺序与规则来源

1. 先读根目录 `CLAUDE.md`、`BASELINE.md`；涉及前端再读前端 `CLAUDE.md`、`DESIGN.md`、`AGENTS.md`。
2. 查看实际 Git 根、工作树、相关 POM/包管理配置，再定位本次涉及的设计、实现和测试；保留已有未提交修改。
3. 用户本次明确要求优先于本地约定。在本地文件中，`CLAUDE.md` 是业务约束来源，`DESIGN.md` 是视觉正本；本文件补充工程做法，不另造审核策略。
4. 业务以最新明确确认的设计为准；依赖版本与已有 API 以当前代码、POM、锁文件为准。外部文档用于核验语义，不能覆盖本项目身份、隔离、闸门规则。
5. 同一规则冲突时先核对更新后的正本；不要沿用旧审查报告中的已修复结论。未决业务选择只阻塞依赖它的实施，继续完成不受影响的工作，不把普通修复或只读检查变成额外审批流程。
6. 涉及库 API 或版本差异，优先用 Context7：先 resolve library，再用返回的 ID 查询，并指定实际版本；缺失时查官方文档。不得以“最新示例”作为升级依赖的授权。

规范依据、检查日期、差异与官方来源见 [技术栈与规范对齐说明](docs/development/standards-alignment.md)。

## 2. 技术基线与工程现状

以下是 2026-09-21 核对结果；开工时重新读取配置，不把本表当永久版本锁。

| 项 | 本地事实/约束 |
|---|---|
| 后端基线 | 芋道 `ruoyi-vue-pro` 的 `master-jdk17`，上游提交见 `BASELINE.md` |
| Java | 根 POM `java.version=17`、source/target=17；使用 JDK 21 运行不等于允许 Java 21 专属语法/API |
| Spring Boot | 根 POM 与 dependencies BOM 为 `3.5.15`；依赖与 Spring Framework 版本随 BOM，不单独覆盖 |
| ORM | MyBatis Plus `3.5.17`；复用本仓库 Mapper、分页、租户与数据权限插件 |
| AI | `chaoyue-module-ai/pom.xml`：Spring AI `1.1.8`、Spring AI Alibaba `1.1.2.2` |
| 构建 | Maven 多模块；`groupId=com.chaoyue`，模块/坐标前缀 `chaoyue-` |
| Java 包 | 暂保留 `cn.iocoder.yudao.*`；新业务包按 `CLAUDE.md` 放 `cn.iocoder.yudao.module.marketing`，未迁移前勿混入第二套根包 |
| 前端方向 | Vue 3 + TypeScript + Element Plus + Pinia + Vue Router + Vite；pnpm |
| 前端现状 | 当前前端目录缺 `package.json`、锁文件和完整工程，精确版本、Node/pnpm 版本及检查脚本尚未落定 |
| 数据与基础设施 | 设计目标为 MySQL 8、Redis、私有对象存储；部署版本须另核对，不等于本机已有可用服务 |
| 尚未落地 | `chaoyue-module-marketing` 尚未建立；未在 POM 中发现 Flyway 接入，迁移不能声明为自动执行 |

- 本产品采用芋道的实现约定；若依只作分层、权限、模块组织等原则参考。不要移植 `AjaxResult/TableDataInfo/startPage/@ss.hasPermi` 替换本仓库的 `CommonResult/PageResult/BaseMapperX/@ss.hasPermission`。
- 不引入 Vue 2/Element UI/Vuex 的代码或配置；不因 RuoYi-AI 示例擅自添加 LangChain4j 或 Python 服务。保留 Spring AI 主线。
- 不顺手升级依赖、重命名根包、删除模块或重建工程。确需此类变更时单独说明范围、兼容性和验证计划。

## 3. 后端分层与接口规范

- Controller：路由、请求校验、方法权限、VO 转换、调用应用服务；不直接用 Mapper，不编排事务，不决定状态机跃迁。
- Service/ServiceImpl：业务规则、对象归属、状态迁移、版本、事务和审计。普通 CRUD 沿用邻近模块的结构；不得为了“分层”机械新增无职责的 Manager/Repository 层。
- Mapper/DO：持久化与查询。复用 `BaseMapperX`、`LambdaQueryWrapperX`、`BaseDO/TenantBaseDO`，自定义 SQL 同样遵守隔离规则。
- 跨业务包按 `CLAUDE.md` 通过 `marketing-api` 的 `*Api`/DTO 协作；该模块尚未创建，不得引用不存在的类冒充完成实现。禁止跨包直接依赖 Mapper。
- 公共业务 DTO/版本快照与 Controller 的请求 VO、数据库 DO 区分；普通 CRUD 转换沿用已有 `BeanUtils`/MapStruct 约定。写入 VO 不接收可由服务端确定的 tenant、creator、审批状态等敏感字段。
- 使用 `*SaveReqVO/*PageReqVO/*RespVO/*DO/*Mapper/*Service` 等已有命名。Controller/接口补 `@Tag/@Operation/@Schema` 与明确的参数/错误/权限说明。
- 请求使用 `jakarta.validation` 校验，参考本地 `@Validated`、`@Valid` 用法；嵌套对象与列表明确校验深度/大小。前端校验不能替代服务端校验。
- 返回 `CommonResult<T>{code,data,msg}`，分页 `PageResult<T>{list,total}`；业务异常通过现有 `ErrorCodeConstants` 与 `ServiceExceptionUtil.exception(...)` 处理，不 catch 后伪造 success。
- 错误码使用 marketing 设计分段，先查重。`CONTENT_VERSION_CONFLICT` 等符号、业务数字码、HTTP 409 是三个不同概念：契约须映射清楚，不能假设 CommonResult 自动设置 HTTP 状态。
- Controller 的 `/marketing/...` 路径由框架根据 `controller.admin/app` 自动加 API 前缀，避免再手工加一遍。前端路由 `/admin/**`、`/tenant/**` 与 HTTP API 前缀不是同一层。
- 权限标识沿用 `模块:资源:动作`，菜单、角色、接口和前端按钮一致；平台写权限还须检查平台身份，不能只凭租户可授予的菜单权限。
- CRUD 优先参考 infra 代码生成器；生成前检查覆盖范围，生成后补业务权限、版本、状态机及测试。不得把生成结果当成验收通过。

参考本地 `system/notice` 的 Controller、Service、Mapper、VO 与测试，以及 `infra/src/main/resources/codegen/`；它们用于核对结构，不能用普通 CRUD 的宽松做法替代本产品闸门。

## 4. Java、Spring 与数据库要求

- Java 类用 UpperCamelCase，成员/方法 lowerCamelCase，常量 UPPER_SNAKE_CASE；包名小写，文件 UTF-8。沿用已有格式，不全仓重排。
- 使用具体泛型，避免 raw type、业务 `Map<String,Object>` 和不必要的反射。金额/精确比例使用 `BigDecimal` 并定义 scale/舍入，禁止用 double 作金额结算。
- 时间使用 `java.time`；任务、规则有效期、导出明确时区与序列化格式，测试使用可控 Clock。不要混用秒、毫秒、无时区字符串。
- 明确 null 与空集合含义；字符串比较不用 `==`；不在含敏感字段的 DO/DTO 上无审查地输出 `toString()`。
- 关闭文件/流等资源使用 try-with-resources；异常保留 cause，禁止空 catch、`printStackTrace` 或把密钥/客户正文写日志。
- 注入风格跟随邻近代码及生成器（当前大量 `jakarta.annotation.Resource`）；新增独立服务可采用构造器注入，但不要只为风格重写旧模块。
- 事务放 Service 边界。需要多次写入保持一致时明确 `@Transactional` 的回滚范围；默认 checked exception 不自动回滚，按业务使用 `rollbackFor`。代理模式的同类自调用不能作为新事务生效的依据。
- AI 调用、远程抓取、文件扫描不占用长数据库事务；先持久化任务，提交后由可恢复 worker 执行，再用短事务写结果。线程池设上限、超时与拒绝处理，禁止无界异步或在 common pool 隐式继承租户上下文。
- SQL 使用绑定参数；排序列/方向采用白名单；禁止把用户输入拼入 `${}`、SQL 片段或任意筛选表达式。
- 为 tenant+筛选/排序组合设计索引；避免 N+1 和无界列表；批量导出分页处理，并逐层保留权限过滤。
- 业务表前缀 `mkt_`，基线 `system_/infra_/ai_` 不改名。租户表的唯一约束考虑 tenant 与软删除语义，跨租户引用在服务层校验。
- 版本迁移按架构采用 Flyway 的方向；首次接入需明确 schema baseline、迁移目录、执行入口、校验与失败恢复。未接入前不得仅保存 SQL 就声称迁移生效；禁止修改已执行迁移。

## 5. 身份、租户、文件与密钥

- **鉴权兼容性必须实际验证：** 当前 system 登录签发 ADMIN 用户类型，app-api 默认期待 MEMBER。新增用户端接口前完成设计中的受控适配与回归，不得删除全局类型检查或 `permitAll` 绕过。
- 登录前按已确认的域名/租户发现流程建立上下文；登录后业务租户以校验后的 Token 身份为准。请求头/参数中的租户值不能赋予访问权，异步 worker 从持久化任务恢复上下文并在 finally 清理。
- 私有业务 DO 使用 `TenantBaseDO` 和 `tenant_id`，同时保留角色/本人/部门等数据范围；两者不能互相替代。
- 公共表忽略租户过滤必须配套平台写权限与只读发布视图；公共和私有提示词优先按设计分表。若采用共表，必须有可验证的专用过滤，不得整表忽略后依赖前端筛选。
- 按 ID 查询/修改、关联对象、详情版本、批量操作、导出、统计、Redis Key、对象存储都检查归属；内部运营使用普通业务租户，不能借超管完成日常创作。
- 文件路径不是权限。敏感文件不得直接复用匿名下载入口；上传对象键由服务端生成，登记、扫描、引用、读取各阶段检查租户、用途与状态。预签名地址短期有效，签发前校验授权。
- 租户自带模型 Key 要有归属、加密、掩码、轮换/撤销设计；不得把当前 AI Key CRUD 当作已具备隔离与信封加密。只能按租户授权选取凭据，不按供应商随机/首条挑选。
- 日志记录 traceId、tenant、动作、对象/版本与安全错误码，不记录 Token、Key、预签名凭据或客户正文。平台跨租户操作按设计二次确认、鉴权与审计。

## 6. 审核、版本与异步任务硬约束

以下摘要来自当前 `CLAUDE.md`，业务策略变更时同步两个入口及前端契约，不另建第二套公式。

- `canApprove/canCopy/canExport` 独立判定；审批检查当前审核人与内容版本创建人，复制/导出检查操作者权限和版本证据。
- 当前默认 `selfApprove=false`；reviewer 恒不可自审；允许自审时记录 `selfApproved=true`。出海默认事实+语言双审。若正式设计仍标“待确认”，不能把本文件当作产品决策已获批准的凭据。
- 放行需要该版本预检通过、所有必需审批完成、导出时规则仍有效、披露 confirmed、受控标识完整；标识豁免按权限、原因、审批、审计处理，旧放行结果失效。
- GateResult 包含 `canApprove/canCopy/canExport/reasons[]/contentVersionId/contentHash/policySnapshot`；接口执行时重新检查，不信任之前 GET 返回或客户端布尔值。
- 正文修改产生新版本；审批/导出绑定版本与哈希，请求带 expectedVersionId，冲突拒绝覆盖。新内容必须完成所需预检和审核，历史审批与 ExportManifest 不改写。
- 事实、术语、提示词、规则和内容包采用主表+不可变版本+使用快照；事实空值不得补写 MOQ、价格、交期、认证、产能等。
- 任务执行与渠道内容审核区分；覆盖 queued/running/prechecking/precheck_failed/pending_review/partial_success/failed/cancelled 等设计状态。状态跃迁只在 Service，取消后的迟到结果丢弃。
- 幂等作用域 tenantId+action+key，并绑定请求摘要；同键异参冲突。租约/attempt 用条件更新防旧 worker 覆盖新结果；计量与供应商真实调用留痕分开。
- 模型调用统一经 `marketing/aigateway`；Schema 校验失败最多重试 2 次，不伪造兜底成功。受控单 URL 解析仅由 ingestion 经 SafeHttpFetcher 执行；不批量抓取、不绕过登录/验证码。

## 7. 测试与验证

- 用仓库 JUnit 5、Mockito 和 `BaseDbUnitTest` 等已有设施；纯逻辑单测、持久层测试、真实鉴权集成测试分层。Mockito 单测不能证明租户插件或安全过滤链生效。
- 每个新业务接口至少一条跨租户越权用例；状态机测试覆盖合法/非法跃迁。涉及闸门至少覆盖未审不可复制、不可自审、披露待确认不可导出，并按改动增加双审、规则过期、版本冲突用例。
- 权限与版本变更验证详情/导出/附件路径；异步变更验证取消后回包、重试幂等、租约抢占、多渠道部分失败。
- MySQL 特有约束、JSON/排序行为不能只用 H2 证明；必要集成测试使用独立测试库，禁止测试连接生产。
- 文档或低影响可逆修改只做相应校验；不为格式改动增设业务测试。功能修改先运行相关测试，必要时再扩至模块/服务，不以跳过测试的 package 宣称验证通过。

后端从仓库根执行，按实际改动选择命令：

```bash
java -version
mvn -version
mvn -pl chaoyue-module-system -am test
mvn -pl chaoyue-server -am verify
```

`chaoyue-module-system` 是已存在模块的示例；改动其他模块时替换为真实模块路径。不要对尚不存在的 marketing 模块执行构建。记录退出码、测试数量与失败原因；若没有执行测试，明确写“未执行”。前端检查见其 AGENTS.md。

## 8. 交付与维护

- 只改本次范围，避免覆盖其他人的改动；不自动提交、推送或部署。
- 在真实 Git 根执行 `git diff --check`，新建未跟踪文件另查 UTF-8、链接和格式；检查通过不等于集成/生产验收。
- 报告写清改了什么、依据、已执行检查、未验证事项及具体阻塞原因，不用“全部完成”代替证据。
- 引用/移植第三方代码检查并维护 `THIRD_PARTY_NOTICES.md`；只读参考无需伪造代码移植记录。
- 依赖、目录、脚本、规则正本变化时同步规范。`AGENTS.md` 与 `CLAUDE.md` 共同维护，不复制整份 PRD 或长期保留过时的审查结论。
