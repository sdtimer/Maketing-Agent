<template>
  <div class="shell-empty">
    <h1>{{ title }}</h1>
    <p>{{ reason }}</p>
    <p>{{ next }}</p>
    <ShellProbe v-if="probe" />
  </div>
</template>
<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import ShellProbe from './ShellProbe.vue'

defineOptions({ name: 'ShellPage' })

const copy: Record<string, { reason: string; next: string; probe?: boolean }> = {
  TenantHome: { reason: '这个租户还没有内容资产。', next: '下一步：去维护业务资产（M1）。', probe: true },
  TenantAssets: { reason: '还没有产品、方案或案例。', next: '下一步：M1 再维护业务资产。' },
  TenantDomestic: { reason: '这个渠道还没有精选批次。', next: '下一步：请平台运营收录。' },
  TenantOverseas: { reason: '这个渠道还没有精选批次。', next: '下一步：请平台运营收录。出海不另做一套壳。' },
  TenantLibrary: { reason: '还没有已授权素材。', next: '下一步：上传时须勾选授权。' },
  TenantPrompts: { reason: '还没有已审提示词。', next: '下一步：未审提示词不能用于创作。' },
  TenantCreate: { reason: '还不能生成内容包。', next: '下一步：M2 开放创作。' },
  TenantTasks: { reason: '还没有待排版任务。', next: '下一步：终审前不能复制。' },
  TenantGuide: { reason: '教程由平台预置，后台编辑延期。', next: '下一步：按步骤进入真实页面。' },
  TenantSettings: { reason: '成员与用量还是空壳。', next: '下一步：审核角色不可自审。' },
  AdminHome: { reason: '这个渠道还没有精选批次。', next: '下一步：请平台运营收录。', probe: true },
  AdminIngest: { reason: '三种录入还没开放。', next: '下一步：M1 路径 A。' },
  AdminAccounts: { reason: '还没有对标对象。', next: '下一步：先选市场和渠道。' },
  AdminSamples: { reason: '还没有内容样本。', next: '下一步：只展示原始互动数字，不算热度分。' },
  AdminBatches: { reason: '还没有批次。', next: '下一步：通过后才能入批次。' },
  AdminReview: { reason: '还没有待审条目。', next: '下一步：未发布条目用户端不可见。' },
  AdminTaxonomy: { reason: '行业和主题是两个维度。', next: '下一步：不合并成一棵树。' },
  AdminPrompts: { reason: '公共提示词还没发布。', next: '下一步：用户端只读已发布版本。' }
}

const route = useRoute()
const page = computed(() => copy[String(route.name)] || { reason: '这一页还没有业务数据。', next: '下一步：M1 再开放。' })
const title = computed(() => (route.meta.title as string) || '工作台')
const reason = computed(() => page.value.reason)
const next = computed(() => page.value.next)
const probe = computed(() => Boolean(page.value.probe))
</script>
