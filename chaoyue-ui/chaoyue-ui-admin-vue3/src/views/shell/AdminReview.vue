<template>
  <section class="review">
    <header>
      <div>
        <h1>数据审核与发布</h1>
        <p>仅审核已校对并提交的收录任务；创建人不能审核自己。</p>
      </div>
      <el-button :loading="loading" @click="load">刷新</el-button>
    </header>
    <el-alert type="warning" :closable="false" show-icon>
      审核通过只表示可进入精选编排，仍不会自动发布到租户端。
    </el-alert>
    <el-radio-group v-model="status" class="status-filter" @change="load">
      <el-radio-button label="pending_review">待审</el-radio-button>
      <el-radio-button label="parsed_draft">草稿/打回</el-radio-button>
      <el-radio-button label="approved">已通过</el-radio-button>
      <el-radio-button label="">全部</el-radio-button>
    </el-radio-group>
    <el-card shadow="never" class="card">
      <el-table v-loading="loading" :data="tasks" empty-text="没有符合筛选条件的收录任务">
        <el-table-column prop="id" label="任务" width="90">
          <template #default="s">#{{ s.row.id }}</template>
        </el-table-column>
        <el-table-column prop="method" label="方式" />
        <el-table-column prop="market" label="市场" width="90" />
        <el-table-column prop="channel" label="渠道" />
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="createTime" label="提交前创建时间" min-width="180" />
        <el-table-column label="操作" width="240">
          <template #default="s">
            <el-button text type="success" :disabled="s.row.status !== 'pending_review'" @click="approve(s.row)">通过</el-button>
            <el-button text type="danger" :disabled="s.row.status !== 'pending_review'" @click="openReject(s.row)">打回</el-button>
            <el-button text @click="openHistory(s.row.id)">记录</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="historyVisible" title="审核记录" width="640px">
      <el-table :data="history" empty-text="暂无审核记录">
        <el-table-column prop="decision" label="结论" width="100" />
        <el-table-column prop="targetVersionId" label="版本" width="80" />
        <el-table-column prop="reasonCode" label="原因" />
        <el-table-column prop="comment" label="说明" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </el-dialog>
    <el-dialog v-model="rejectVisible" title="打回收录任务" width="440px">
      <el-form label-position="top">
        <el-form-item label="原因代码" required>
          <el-input v-model="reasonCode" placeholder="如 source_incomplete" />
        </el-form-item>
        <el-form-item label="审核说明" required>
          <el-input v-model="comment" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="acting" @click="reject">确认打回</el-button>
      </template>
    </el-dialog>
  </section>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  approvePlatformReview,
  getIngestionTaskPage,
  getPlatformReviewRecords,
  rejectPlatformReview,
  type IngestionTaskPageItem,
  type PlatformReviewRecordVO
} from '@/api/marketing/ingestion'

defineOptions({ name: 'AdminReview' })

const loading = ref(false)
const acting = ref(false)
const tasks = ref<IngestionTaskPageItem[]>([])
const rejectVisible = ref(false)
const historyVisible = ref(false)
const history = ref<PlatformReviewRecordVO[]>([])
const reasonCode = ref('')
const comment = ref('')
const selected = ref<IngestionTaskPageItem>()
const status = ref('pending_review')

const load = async () => {
  loading.value = true
  try {
    tasks.value = (await getIngestionTaskPage({ pageNo: 1, pageSize: 100, status: status.value || undefined })).list
  } finally {
    loading.value = false
  }
}

const approve = async (item: IngestionTaskPageItem) => {
  acting.value = true
  try {
    await approvePlatformReview(item.id, item.version)
    ElMessage.success('审核通过，可进入精选编排')
    await load()
  } finally {
    acting.value = false
  }
}

const openReject = (item: IngestionTaskPageItem) => {
  selected.value = item
  reasonCode.value = ''
  comment.value = ''
  rejectVisible.value = true
}

const openHistory = async (id: number) => {
  history.value = await getPlatformReviewRecords(id)
  historyVisible.value = true
}

const reject = async () => {
  if (!selected.value || !reasonCode.value.trim() || !comment.value.trim()) {
    ElMessage.warning('请填写原因代码和审核说明')
    return
  }
  acting.value = true
  try {
    await rejectPlatformReview(selected.value.id, selected.value.version, reasonCode.value, comment.value)
    ElMessage.success('已打回收录任务')
    rejectVisible.value = false
    await load()
  } finally {
    acting.value = false
  }
}

onMounted(load)
</script>
<style scoped>
.review {
  max-width: 1060px;
  padding: 8px 8px 32px;
}
.review header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 18px;
}
.review h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.review p {
  margin: 0;
  color: #6b7280;
}
.status-filter {
  margin-top: 16px;
}
.card {
  margin-top: 18px;
}
</style>
