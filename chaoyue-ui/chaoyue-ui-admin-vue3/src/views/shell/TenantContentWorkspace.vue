<template>
  <section class="workspace">
    <header>
      <div>
        <h1>智能创作</h1>
        <p>保存正文会创建新版本并重新进入终审。旧版本不会被覆盖。</p>
      </div>
      <el-tag type="warning" effect="plain">M2 · 人工终审后才可复制/导出</el-tag>
    </header>
    <el-card shadow="never">
      <template #header>{{ editingId ? `编辑内容包 #${editingId}` : '新建内容包' }}</template>
      <el-form :model="form" label-position="top">
        <div class="grid">
          <el-form-item label="任务名称"><el-input v-model="form.title" maxlength="256" :disabled="Boolean(editingId)" /></el-form-item>
          <el-form-item label="渠道">
            <el-select v-model="form.channel" :disabled="Boolean(editingId)">
              <el-option label="公众号" value="wechat" />
              <el-option label="小红书" value="xiaohongshu" />
              <el-option label="抖音" value="douyin" />
            </el-select>
          </el-form-item>
          <el-form-item label="市场">
            <el-radio-group v-model="form.market" :disabled="Boolean(editingId)">
              <el-radio-button label="domestic">国内</el-radio-button>
              <el-radio-button label="overseas">出海</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="事实快照 JSON">
            <el-input v-model="form.factSnapshot" :disabled="Boolean(editingId)" />
          </el-form-item>
        </div>
        <el-form-item label="正文">
          <el-input v-model="form.content" type="textarea" :rows="8" maxlength="200000" show-word-limit />
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="submit">{{ editingId ? '保存新版本并重审' : '保存并提交终审' }}</el-button>
        <el-button v-if="editingId" @click="reset">取消编辑</el-button>
      </el-form>
    </el-card>
    <el-card class="list" shadow="never">
      <template #header>
        <span>我的内容包</span>
        <el-button text type="primary" @click="load">刷新</el-button>
      </template>
      <el-table v-loading="loading" :data="packages" empty-text="暂无内容包">
        <el-table-column prop="id" label="内容包" width="100">
          <template #default="s">#{{ s.row.id }}</template>
        </el-table-column>
        <el-table-column prop="channel" label="渠道" />
        <el-table-column prop="currentVersionId" label="当前版本" />
        <el-table-column prop="status" label="状态">
          <template #default="s">
            <el-tag :type="s.row.status === 'approved' ? 'success' : 'warning'">{{ s.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="120">
          <template #default="s">
            <el-button text type="primary" @click="edit(s.row.id)">编辑正文</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createContentPackage,
  getContentPackage,
  getContentPackageList,
  saveContentVersion,
  type ContentPackage
} from '@/api/marketing/content'

defineOptions({ name: 'TenantContentWorkspace' })

const loading = ref(false)
const saving = ref(false)
const editingId = ref<number>()
const packages = ref<ContentPackage[]>([])
const form = reactive({ title: '', market: 'domestic', channel: 'wechat', content: '', factSnapshot: '{}' })

const load = async () => {
  loading.value = true
  try {
    packages.value = await getContentPackageList()
  } finally {
    loading.value = false
  }
}

const reset = () => {
  editingId.value = undefined
  form.title = ''
  form.content = ''
  form.factSnapshot = '{}'
}

const edit = async (id: number) => {
  const detail = await getContentPackage(id)
  editingId.value = id
  form.content = detail.content || ''
  form.channel = detail.channel
}

const submit = async () => {
  if (!form.content.trim() || (!editingId.value && !form.title.trim())) {
    ElMessage.warning('请填写任务名称和正文')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await saveContentVersion(editingId.value, form.content)
      ElMessage.success('已保存新版本，需重新终审')
    } else {
      await createContentPackage({ ...form })
      ElMessage.success('已创建内容包，等待非创建人终审')
    }
    reset()
    await load()
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
<style scoped>
.workspace {
  max-width: 1060px;
  padding: 8px 8px 32px;
}
.workspace header {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 18px;
}
.workspace h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.workspace p {
  margin: 0;
  color: #6b7280;
}
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}
.grid :deep(.el-select) {
  width: 100%;
}
.list {
  margin-top: 18px;
}
.list :deep(.el-card__header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
@media (max-width: 720px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .workspace header {
    display: block;
  }
}
</style>
