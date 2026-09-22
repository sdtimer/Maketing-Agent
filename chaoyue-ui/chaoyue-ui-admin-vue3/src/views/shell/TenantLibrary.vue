<template>
  <section class="library">
    <header>
      <div>
        <h1>我的素材</h1>
        <p>上传时必须勾选授权。未授权素材不能入库，也不能进入创作。</p>
      </div>
    </header>
    <el-card shadow="never">
      <template #header>登记已授权素材</template>
      <el-form :model="form" label-position="top">
        <div class="grid">
          <el-form-item label="素材名称"><el-input v-model="form.name" maxlength="256" /></el-form-item>
          <el-form-item label="来源"><el-input v-model="form.source" placeholder="未录入 · 不可推测" /></el-form-item>
          <el-form-item label="使用范围">
            <el-select v-model="form.usageScope">
              <el-option label="仅内部" value="internal" />
              <el-option label="营销投放" value="marketing" />
            </el-select>
          </el-form-item>
          <el-form-item label="文件">
            <el-upload :file-list="files" :limit="1" :http-request="upload" :on-remove="() => (form.objectKey = '')">
              <el-button :loading="uploading" plain>选择文件</el-button>
            </el-upload>
          </el-form-item>
        </div>
        <el-form-item>
          <el-checkbox v-model="form.authorized">我确认该素材已获授权，允许用于营销内容</el-checkbox>
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="submit">入库</el-button>
      </el-form>
    </el-card>
    <el-card class="list" shadow="never">
      <template #header>
        <span>已授权素材</span>
        <el-button text type="primary" @click="load">刷新</el-button>
      </template>
      <el-table v-loading="loading" :data="assets" empty-text="还没有已授权素材。">
        <el-table-column prop="id" label="素材" width="90">
          <template #default="s">#{{ s.row.id }}</template>
        </el-table-column>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="source" label="来源">
          <template #default="s">{{ s.row.source || '未录入 · 不可推测' }}</template>
        </el-table-column>
        <el-table-column prop="usageScope" label="范围" />
        <el-table-column label="授权" width="90">
          <template #default="s">
            <el-tag :type="s.row.authorized ? 'success' : 'danger'">{{ s.row.authorized ? '已授权' : '未授权' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type UploadRequestOptions, type UploadUserFile } from 'element-plus'
import { updateFile } from '@/api/infra/file'
import { createAuthorizedAsset, getAuthorizedAssets, type AuthorizedAsset } from '@/api/marketing/asset'

defineOptions({ name: 'TenantLibrary' })

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const assets = ref<AuthorizedAsset[]>([])
const files = ref<UploadUserFile[]>([])
const form = reactive({ name: '', objectKey: '', source: '', usageScope: 'internal', authorized: false })

const load = async () => {
  loading.value = true
  try {
    assets.value = await getAuthorizedAssets()
  } finally {
    loading.value = false
  }
}

const upload = async (options: UploadRequestOptions) => {
  uploading.value = true
  try {
    form.objectKey = await updateFile({ file: options.file, directory: 'marketing/library' })
    options.onSuccess?.(form.objectKey)
  } finally {
    uploading.value = false
  }
}

const submit = async () => {
  if (!form.name.trim() || !form.objectKey) return ElMessage.warning('请填写名称并上传文件')
  if (!form.authorized) return ElMessage.warning('未授权素材不能入库')
  saving.value = true
  try {
    await createAuthorizedAsset({ ...form, authorized: true })
    ElMessage.success('已登记授权素材')
    form.name = ''
    form.objectKey = ''
    form.source = ''
    form.authorized = false
    files.value = []
    await load()
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
<style scoped>
.library {
  max-width: 1060px;
  padding: 8px 8px 32px;
}
.library header {
  margin-bottom: 18px;
}
.library h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.library p {
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
}
</style>
