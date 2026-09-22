# 智造营销 SaaS · 前端 Agent 开发规范

本文件仅作用于当前前端目录及其子目录，补充仓库根 `AGENTS.md`。开工先读本目录 `CLAUDE.md` 与 `DESIGN.md`，两者分别是交互/业务约束与视觉正本。

## 1. 当前工程状态与版本

- 目标技术栈为 Vue 3、TypeScript、Element Plus、Pinia、Vue Router、Vite、pnpm；不采用 Vue 2、Element UI、Vuex 或若依旧版 webpack 配置。
- 2026-09-21 实查：本目录只有规范、README 与少量历史 MES 文件；缺 package.json、锁文件、完整路由/request/store 等工程。因此还不能运行或声称通过 lint、vue-tsc、build。
- 完整工程接入时以本目录 README 指向的芋道 Vue3+Element Plus 仓库为来源，记录上游提交、packageManager、engines、lockfile。不能把上游当前版本或架构规划表当作本地已安装版本。
- 不因“编写页面”擅自新建另一套脚手架。初始化确在任务范围内时先明确基线兼容性，保留现有 CLAUDE.md、DESIGN.md 和本文件，避免覆盖规范。
- 框架 API 先核对本地版本，再查询 Context7/官方文档。例如 `onWatcherCleanup` 需 Vue 3.5+；版本未锁定时不能无条件使用。

## 2. 目录与职责

完整工程建立后沿用芋道结构；以下路径是目标约定，不表示当前均已存在。

| 路径 | 职责 |
|---|---|
| `src/api/marketing/<domain>/` | 请求函数、请求/响应类型；页面通过此层调用 |
| `src/views/marketing/<domain>/` | 路由页面、该领域专用 components |
| `src/components/` | 跨领域通用组件；避免把单页业务组件提升为全局 |
| `src/hooks/` | 复用组合逻辑，`useXxx` 命名；已接入工程若采用 composables 则跟随既有约定 |
| `src/store/` | 跨页面共享状态；临时表单/弹窗状态留在页面 |
| `src/config/axios/` | 芋道 request 实现、拦截器、超时与错误处理 |
| `src/router/`、`src/layout/` | 路由、权限守卫与两套布局 |
| `src/styles/` | 全局 token、基础样式；局部样式写组件 scoped |

- 参考后端 `chaoyue-module-infra/src/main/resources/codegen/vue3/` 的模板；默认 `@/config/axios`，不要复制若依旧版 `@/utils/request.js` 作为并行客户端。
- 原型用于验证页面动作、信息密度与品牌；不把原型的内联脚本、假数据、角色切换器或固定状态复制为生产实现。

## 3. Vue 3 与 TypeScript

- 新 SFC 默认 `<script setup lang="ts">` + Composition API；仅在实际使用 JSX 且工程已支持时使用 TSX。页面 `index.vue`、组件 `XxxForm.vue/XxxDialog.vue` 等跟随基线约定。
- props/emits/API/表单/分页使用明确类型，输入与响应模型分开。外部不可信结果先作为 unknown 校验，不用 any、非空断言或 `as` 强行绕过错误。
- 不直接修改 props；经 emits 或明确双向绑定契约通知父组件。v-for 使用稳定业务 key，不用数组索引绑定可编辑行。
- 展示派生值用 computed；网络调用、定时器等副作用放 watch/生命周期/组合函数，不放 computed。不要为同一数据建立多个需要手工同步的副本。
- 注意 reactive/props 解构的响应性与版本差异；不把普通属性解构误当成响应式引用。
- 请求结束在 finally 复原 loading；表单校验后提交，提交期间阻止误重复，但仍保留服务端幂等。
- 渠道、租户、路由快速切换时取消旧请求或用序号忽略迟到响应，避免旧数据覆盖新页面；watch 回调提供的 onCleanup 可作版本兼容方案。若用 onWatcherCleanup，确认 Vue 3.5+ 且在 await 之前同步注册。
- 页面卸载清理 watch、计时器、事件与订阅。前端停止轮询不代表后端任务取消，取消必须调用明确接口并展示结果。
- Pinia 用于登录/角色/字典/必要共享状态；退出或切换租户清理业务缓存、列表、订阅与动态路由。不得通过改 localStorage 中 tenantId 获得身份。
- 遵循实际 ESLint/Prettier/Stylelint/tsconfig；缺配置时先完成工程基线，不为消除报错批量关闭 strict 或禁用规则。

## 4. API、权限与错误契约

- 页面不直接 fetch/axios；统一 API 层调用 request。读取其解包逻辑后确定返回类型，禁止重复 `data.data` 或混用若依 `rows/total` 与芋道 `list/total`。
- 路径必须与后端契约一致；HTTP API 的 `/admin-api`/`/app-api` 前缀只配置一次，不能依靠页面所属布局猜测 Token 用户类型。
- Java Long 超出 JS 安全整数范围时用双方约定的字符串传输；金额/精确小数按后端契约表达，禁止静默浮点运算。日期、时区、空值与单位明确约定。
- 统一处理未登录、无权限、业务校验、版本冲突、幂等冲突、网络超时，区分 HTTP 状态与 CommonResult.code。不得遇到失败继续展示旧的“成功”通知。
- 前端角色/按钮/路由控制只是体验；后端仍须校验。业务按钮消费服务端 GateResult，不从 approved、角色名称或审核人自行推导。
- GateResult 未返回、请求失败或绑定版本变化时按不可操作呈现并说明原因，不能沿用旧版本可导出结果。
- 审核/编辑/导出传 expectedVersionId；冲突后刷新并让用户处理差异，不自动覆盖或静默重提。
- 公共素材浏览携带 market+channel，无“全部渠道”；切换清空列表、分页与不适用选择，并防迟到响应覆盖。

## 5. 产品组件与安全

- 共用 GateCopy、ChannelSwitch、StatusTag、FactEmpty、IngestMethod、AuditBar、ReadonlyBody、EmptyHint；这些是待实现/复用的契约，先查是否存在，不伪造组件已经可用。
- GateCopy/AuditBar 仅消费服务端 canApprove/canCopy/canExport 与 reasons；不另写第二套闸门。每次真正执行动作仍经后端校验。
- 事实空值显示“未录入 · 不可推测”；金额、MOQ、认证等不得前端默认补零或交给模型填充。
- 排版正文只读，禁止 contenteditable；改正文走新版本流程。预览、复制、导出使用同一版本的服务端受控渲染内容。
- 模型/上传/外部 HTML 不编译为 Vue 模板，不直接 v-html 注入主页面；预览按设计使用服务端净化后的 sandbox iframe，不开放脚本执行等不必要能力。
- 上传前的类型/大小检查只是体验；扫描状态与对象归属由服务端确认。下载使用已授权地址，不能自己拼接别人的对象键或永久公开 URL。
- 不把 API Key、Token、客户原文写入 console、错误监控、埋点、URL 或截图演示数据；Key 只显示后端掩码。
- URL/CTA 协议按契约校验，禁止 javascript: 等可执行协议；离开站点的新窗口链接设置合适的 rel。

## 6. 视觉、交互与可访问性

- 以 DESIGN.md token 与布局为准：用户端浅色 TenantLayout，平台端深色 AdminLayout；主色 #2563eb，品牌橙仅按品牌规范使用。
- 横版 LOGO 用于登录/用户端顶栏；仅需图标时使用图标资产，勿裁切横版；不复用桌面产品的陶土视觉。
- 页面具备 loading、empty、error、success、permission-denied 状态；空态包含原因与下一步；不显示伪造进度百分比。
- 表单有 label、清晰必填/错误信息；图标按钮有可访问名称；键盘焦点可见，弹窗关闭恢复焦点；状态用文字与颜色共同表达。
- 1440×900 与 1280 宽度验证后台密度；尊重 prefers-reduced-motion。局部样式 scoped，避免随意覆盖 Element Plus 全局样式。
- 公共状态/渠道/打回原因取字典；前端不另维护一套与服务端冲突的中文状态映射。

## 7. 验证与交付

- 当前缺工程清单时明确记录阻塞，不执行不存在的 npm/pnpm 脚本并宣称通过。
- 完整工程到位后先阅读 package.json scripts/engines/packageManager 与锁文件：按锁文件执行安装，只运行已定义的 type-check、lint、stylelint、test、build 脚本。
- 若工程已有 vue-tsc 但未定义类型检查脚本，可用 `pnpm exec vue-tsc --noEmit`，前提是 tsconfig 适配该命令；Vite build 不能代替类型检查。
- 组件/逻辑测试覆盖真实风险：GateResult 禁用、版本冲突、迟到响应、空事实与表单校验；测试工具沿用接入工程，未安装 Vitest/Playwright 时不要写成现有能力。
- UI 变更在 1440/1280 视口检查布局、键盘操作和实际请求结果；涉及核心流程时验证租户创建→生成→预检→审核→导出及相关失败路径。
- 静态截图、Mock、类型检查、构建、真实接口、真实模型分别报告；不以一种证据替代另一种。只改文档时检查链接、格式和约束一致性即可。

官方来源及版本边界见 [技术栈与规范对齐说明](../../docs/development/standards-alignment.md)。
