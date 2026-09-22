<template>
  <section class="workspace">
    <header><div><h1>智能创作</h1><p>创建内容包即保存首个不可覆盖正文版本，并自动进入终审队列。</p></div><el-tag type="warning" effect="plain">M2 · 人工终审后才可复制/导出</el-tag></header>
    <el-card shadow="never">
      <template #header>新建内容包</template>
      <el-form :model="form" label-position="top" @submit.prevent="submit">
        <div class="grid"><el-form-item label="任务名称"><el-input v-model="form.title" maxlength="256" /></el-form-item><el-form-item label="渠道"><el-select v-model="form.channel"><el-option label="公众号" value="wechat"/><el-option label="小红书" value="xiaohongshu"/><el-option label="抖音" value="douyin"/></el-select></el-form-item><el-form-item label="市场"><el-radio-group v-model="form.market"><el-radio-button label="domestic">国内</el-radio-button><el-radio-button label="overseas">出海</el-radio-button></el-radio-group></el-form-item><el-form-item label="事实快照（JSON，可选）"><el-input v-model="form.factSnapshot" /></el-form-item></div><el-form-item label="首版正文"><el-input v-model="form.content" type="textarea" :rows="8" maxlength="200000" show-word-limit /></el-form-item><el-button type="primary" :loading="saving" @click="submit">保存并提交终审</el-button></el-form>
    </el-card>
    <el-card class="list" shadow="never"><template #header><span>我的内容包</span><el-button text type="primary" @click="load">刷新</el-button></template><el-table v-loading="loading" :data="packages" empty-text="暂无内容包"><el-table-column prop="id" label="内容包" width="100"><template #default="s">#{{ s.row.id }}</template></el-table-column><el-table-column prop="channel" label="渠道"/><el-table-column prop="currentVersionId" label="当前版本"/><el-table-column prop="status" label="状态"><template #default="s"><el-tag :type="s.row.status === 'approved' ? 'success' : 'warning'">{{ s.row.status }}</el-tag></template></el-table-column><el-table-column prop="createTime" label="创建时间"/></el-table></el-card>
  </section>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createContentPackage, getContentPackageList, type ContentPackage } from '@/api/marketing/content'
defineOptions({ name: 'TenantContentWorkspace' })
const loading = ref(false); const saving = ref(false); const packages = ref<ContentPackage[]>([])
const form = reactive({ title: '', market: 'domestic', channel: 'wechat', content: '', factSnapshot: '{}' })
const load = async () => { loading.value = true; try { packages.value = await getContentPackageList() } finally { loading.value = false } }
const submit = async () => { if (!form.title.trim() || !form.content.trim()) { ElMessage.warning('请填写任务名称和首版正文'); return }; saving.value = true; try { await createContentPackage({ ...form }); ElMessage.success('已创建内容包，等待非创建人终审'); form.title = ''; form.content = ''; form.factSnapshot = '{}'; await load() } finally { saving.value = false } }
onMounted(load)
</script>
<style scoped>.workspace{max-width:1060px;padding:8px 8px 32px}.workspace header{display:flex;justify-content:space-between;gap:20px;margin-bottom:18px}.workspace h1{margin:0 0 8px;font-size:22px}.workspace p{margin:0;color:#6b7280}.grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:0 18px}.grid :deep(.el-select){width:100%}.list{margin-top:18px}.list :deep(.el-card__header){display:flex;justify-content:space-between;align-items:center}@media(max-width:720px){.grid{grid-template-columns:1fr}.workspace header{display:block}.workspace header .el-tag{margin-top:12px}}</style>
