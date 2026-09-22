<template>
  <section class="review-page">
    <header>
      <div>
        <h1>内容审核</h1>
        <p>审核绑定当前正文版本。创建人不能审核自己；复制/导出由服务端闸门再次校验。</p>
      </div>
      <el-button :loading="loading" @click="load">刷新</el-button>
    </header>
    <el-alert type="warning" :closable="false" show-icon>
      只有 pending_review 可终审。通过后才会开放复制与导出。
    </el-alert>
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="packages" empty-text="暂无内容包">
        <el-table-column prop="id" label="内容包" width="96">
          <template #default="s">#{{ s.row.id }}</template>
        </el-table-column>
        <el-table-column prop="channel" label="渠道" width="110" />
        <el-table-column prop="currentVersionId" label="当前版本" width="110" />
        <el-table-column prop="status" label="状态" width="140">
          <template #default="s">
            <el-tag :type="tagType(s.row.status)">{{ s.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" min-width="280">
          <template #default="s">
            <el-button text type="success" :disabled="s.row.status !== 'pending_review'" @click="approve(s.row)">通过</el-button>
            <el-button text type="danger" :disabled="s.row.status !== 'pending_review'" @click="openReject(s.row)">打回</el-button>
            <el-button text :disabled="s.row.status !== 'approved'" @click="copy(s.row.id)">复制</el-button>
            <el-button text :disabled="s.row.status !== 'approved'" @click="exportHtml(s.row.id)">导出 HTML</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="rejectVisible" title="打回内容包" width="440px">
      <el-form label-position="top">
        <el-form-item label="打回说明" required>
          <el-input v-model="rejectComment" type="textarea" :rows="4" maxlength="1000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="acting" @click="reject">确认打回</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="copyVisible" title="复制终审正文" width="640px">
      <p class="meta">版本 {{ copied?.contentVersionId }} · {{ copied?.contentHash }}</p>
      <el-input :model-value="copied?.content" type="textarea" :rows="12" readonly />
    </el-dialog>
  </section>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  approveContentPackage,
  copyContentPackage,
  exportContentPackageHtml,
  getContentGate,
  getContentPackageList,
  rejectContentPackage,
  type ContentPackage
} from '@/api/marketing/content'

defineOptions({ name: 'TenantContentReview' })

const loading = ref(false)
const acting = ref(false)
const packages = ref<ContentPackage[]>([])
const rejectVisible = ref(false)
const copyVisible = ref(false)
const rejectComment = ref('')
const selected = ref<ContentPackage>()
const copied = ref<{ contentVersionId: number; contentHash: string; content: string }>()

const load = async () => {
  loading.value = true
  try {
    packages.value = await getContentPackageList()
  } finally {
    loading.value = false
  }
}

const tagType = (status: string) =>
  status === 'approved' ? 'success' : status === 'pending_review' ? 'warning' : 'info'

const approve = async (item: ContentPackage) => {
  if (!item.currentVersionId) return
  acting.value = true
  try {
    await approveContentPackage(item.id, item.currentVersionId)
    ElMessage.success('终审通过，已开放复制和导出')
    await load()
  } finally {
    acting.value = false
  }
}

const openReject = (item: ContentPackage) => {
  selected.value = item
  rejectComment.value = ''
  rejectVisible.value = true
}

const reject = async () => {
  if (!selected.value?.currentVersionId || !rejectComment.value.trim()) {
    ElMessage.warning('请填写打回说明')
    return
  }
  acting.value = true
  try {
    await rejectContentPackage(selected.value.id, selected.value.currentVersionId, rejectComment.value)
    ElMessage.success('已打回，等待创建人重新编辑')
    rejectVisible.value = false
    await load()
  } finally {
    acting.value = false
  }
}

const copy = async (id: number) => {
  const gate = await getContentGate(id)
  if (!gate.canCopy) {
    ElMessage.warning(gate.reason || '当前不可复制')
    return
  }
  copied.value = await copyContentPackage(id)
  copyVisible.value = true
}

const exportHtml = async (id: number) => {
  const gate = await getContentGate(id)
  if (!gate.canExport) {
    ElMessage.warning(gate.reason || '当前不可导出')
    return
  }
  const html = await exportContentPackageHtml(id)
  const blob = new Blob([html], { type: 'text/html;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `content-package-${id}.html`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('已导出 HTML，并写入导出清单')
}

onMounted(load)
</script>
<style scoped>
.review-page {
  max-width: 1060px;
  padding: 8px 8px 32px;
}
.review-page header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
}
.review-page h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.review-page p,
.meta {
  margin: 0 0 12px;
  color: #6b7280;
}
.table-card {
  margin-top: 18px;
}
</style>
