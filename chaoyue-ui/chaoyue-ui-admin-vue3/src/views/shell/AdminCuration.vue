<template>
  <section class="curation">
    <header>
      <div>
        <h1>精选批次与排序</h1>
        <p>只有审核通过的条目可入批次；发布后才会在租户精选库出现。</p>
      </div>
    </header>
    <el-card shadow="never">
      <template #header>新建精选批次</template>
      <el-form :model="batchForm" inline>
        <el-form-item label="市场">
          <el-select v-model="batchForm.market" style="width: 120px">
            <el-option label="国内" value="domestic" />
            <el-option label="出海" value="overseas" />
          </el-select>
        </el-form-item>
        <el-form-item label="渠道"><el-input v-model="batchForm.channel" placeholder="wechat" /></el-form-item>
        <el-form-item label="运营说明"><el-input v-model="batchForm.opsNote" placeholder="可选" /></el-form-item>
        <el-button type="primary" :loading="saving" @click="createBatch">创建草稿批次</el-button>
      </el-form>
    </el-card>
    <el-card class="entry-card" shadow="never">
      <template #header>加入审核通过的条目</template>
      <el-form :model="entryForm" label-position="top">
        <div class="entry-grid">
          <el-form-item label="草稿批次">
            <el-select v-model="entryForm.batchId" @change="loadEntries">
              <el-option
                v-for="item in drafts"
                :key="item.id"
                :label="`#${item.id} · ${item.market}/${item.channel}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="审核通过任务">
            <el-select v-model="entryForm.taskId">
              <el-option
                v-for="item in approvedTasks"
                :key="item.id"
                :label="`#${item.id} · ${item.market}/${item.channel}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="排序"><el-input-number v-model="entryForm.sortOrder" :min="0" /></el-form-item>
          <el-form-item label="推荐说明"><el-input v-model="entryForm.recommendText" /></el-form-item>
        </div>
        <el-form-item label="入选理由"><el-input v-model="entryForm.reason" type="textarea" :rows="2" /></el-form-item>
        <el-button type="primary" :loading="entrySaving" @click="addEntry">加入批次</el-button>
      </el-form>
      <el-table class="entry-table" :data="entries" empty-text="请先选择草稿批次">
        <el-table-column prop="id" label="条目" width="90">
          <template #default="s">#{{ s.row.id }}</template>
        </el-table-column>
        <el-table-column prop="taskId" label="任务" width="90" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="recommendText" label="推荐说明" />
        <el-table-column prop="reason" label="入选理由" />
        <el-table-column label="操作" width="220">
          <template #default="s">
            <el-button text :disabled="!canEditEntries || s.$index === 0" @click="move(s.$index, -1)">上移</el-button>
            <el-button text :disabled="!canEditEntries || s.$index === entries.length - 1" @click="move(s.$index, 1)">下移</el-button>
            <el-button text type="danger" :disabled="!canEditEntries" @click="removeEntry(s.row.id)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card class="list" shadow="never">
      <template #header>
        <span>精选批次</span>
        <el-button text type="primary" @click="load">刷新</el-button>
      </template>
      <el-table v-loading="loading" :data="batches" empty-text="暂无批次">
        <el-table-column prop="id" label="批次" width="90">
          <template #default="s">#{{ s.row.id }}</template>
        </el-table-column>
        <el-table-column prop="market" label="市场" />
        <el-table-column prop="channel" label="渠道" />
        <el-table-column prop="opsNote" label="运营说明" />
        <el-table-column prop="status" label="状态">
          <template #default="s">
            <el-tag :type="s.row.status === 'published' ? 'success' : 'info'">{{ s.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="s">
            <el-button text @click="inspect(s.row)">查看条目</el-button>
            <el-button v-if="s.row.status === 'draft'" text type="success" @click="publish(s.row.id)">发布</el-button>
            <el-button v-if="s.row.status === 'published'" text type="danger" @click="offline(s.row.id)">下线</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  addCurationEntry,
  createCurationBatch,
  getCurationBatches,
  getCurationEntries,
  offlineCurationBatch,
  publishCurationBatch,
  removeCurationEntry,
  sortCurationEntry,
  type CurationBatch,
  type CurationEntry
} from '@/api/marketing/curation'
import { getIngestionTaskPage, type IngestionTaskPageItem } from '@/api/marketing/ingestion'

defineOptions({ name: 'AdminCuration' })

const loading = ref(false)
const saving = ref(false)
const entrySaving = ref(false)
const batches = ref<CurationBatch[]>([])
const tasks = ref<IngestionTaskPageItem[]>([])
const entries = ref<CurationEntry[]>([])
const batchForm = reactive({ market: 'domestic', channel: 'wechat', opsNote: '' })
const entryForm = reactive({
  batchId: undefined as number | undefined,
  taskId: undefined as number | undefined,
  sortOrder: 0,
  recommendText: '',
  reason: ''
})
const drafts = computed(() => batches.value.filter((item) => item.status === 'draft'))
const canEditEntries = computed(() => drafts.value.some((item) => item.id === entryForm.batchId))
const approvedTasks = computed(() => tasks.value.filter((item) => item.status === 'approved'))

const load = async () => {
  loading.value = true
  try {
    ;[batches.value, tasks.value] = await Promise.all([
      getCurationBatches(),
      getIngestionTaskPage({ pageNo: 1, pageSize: 100 }).then((result) => result.list)
    ])
    await loadEntries()
  } finally {
    loading.value = false
  }
}

const loadEntries = async () => {
  if (!entryForm.batchId) {
    entries.value = []
    return
  }
  entries.value = await getCurationEntries(entryForm.batchId)
}

const createBatch = async () => {
  if (!batchForm.channel.trim()) return ElMessage.warning('请填写渠道')
  saving.value = true
  try {
    await createCurationBatch({ ...batchForm })
    ElMessage.success('已创建草稿批次')
    await load()
  } finally {
    saving.value = false
  }
}

const addEntry = async () => {
  if (!entryForm.batchId || !entryForm.taskId) return ElMessage.warning('请选择草稿批次和已审核任务')
  entrySaving.value = true
  try {
    await addCurationEntry(entryForm.batchId, {
      taskId: entryForm.taskId,
      sortOrder: entryForm.sortOrder,
      recommendText: entryForm.recommendText,
      reason: entryForm.reason
    })
    ElMessage.success('已加入批次，可继续加入或发布')
    entryForm.taskId = undefined
    entryForm.recommendText = ''
    entryForm.reason = ''
    await loadEntries()
  } finally {
    entrySaving.value = false
  }
}

const removeEntry = async (entryId: number) => {
  if (!entryForm.batchId) return
  await removeCurationEntry(entryForm.batchId, entryId)
  ElMessage.success('已从草稿批次移除')
  await loadEntries()
}

const move = async (index: number, delta: number) => {
  if (!entryForm.batchId) return
  const current = entries.value[index]
  const target = entries.value[index + delta]
  if (!current || !target) return
  const currentSort = current.sortOrder
  await sortCurationEntry(entryForm.batchId, current.id, target.sortOrder)
  await sortCurationEntry(entryForm.batchId, target.id, currentSort)
  await loadEntries()
}

const inspect = async (batch: CurationBatch) => {
  if (batch.status === 'draft') entryForm.batchId = batch.id
  entries.value = await getCurationEntries(batch.id)
}

const publish = async (id: number) => {
  await publishCurationBatch(id)
  ElMessage.success('已发布到租户精选库')
  await load()
}

const offline = async (id: number) => {
  await offlineCurationBatch(id)
  ElMessage.success('已下线')
  await load()
}

onMounted(load)
</script>
<style scoped>
.curation {
  max-width: 1060px;
  padding: 8px 8px 32px;
}
.curation h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.curation p {
  margin: 0 0 18px;
  color: #6b7280;
}
.entry-card,
.list {
  margin-top: 18px;
}
.list :deep(.el-card__header) {
  display: flex;
  justify-content: space-between;
}
.entry-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}
.entry-grid :deep(.el-select) {
  width: 100%;
}
.entry-table {
  margin-top: 16px;
}
@media (max-width: 720px) {
  .entry-grid {
    grid-template-columns: 1fr;
  }
}
</style>
