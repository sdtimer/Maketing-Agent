<template>
  <section class="assets">
    <header>
      <div>
        <h1>组织与业务资产</h1>
        <p>空字段固定显示「未录入 · 不可推测」，保存新版本不会覆盖旧版本。</p>
      </div>
    </header>
    <el-card shadow="never">
      <template #header>{{ editingId ? `更新产品 #${editingId}` : '新增产品' }}</template>
      <el-form :model="form" label-position="top">
        <div class="grid">
          <el-form-item label="产品名称"><el-input v-model="form.name" maxlength="256" /></el-form-item>
          <el-form-item label="公开范围">
            <el-select v-model="form.publicScope">
              <el-option label="受限" value="restricted" />
              <el-option label="内部" value="internal" />
            </el-select>
          </el-form-item>
          <el-form-item label="MOQ"><el-input v-model="form.moq" placeholder="未录入 · 不可推测" /></el-form-item>
          <el-form-item label="交期"><el-input v-model="form.leadTime" placeholder="未录入 · 不可推测" /></el-form-item>
          <el-form-item label="价格"><el-input v-model="form.price" placeholder="未录入 · 不可推测" /></el-form-item>
          <el-form-item label="认证"><el-input v-model="form.certification" placeholder="未录入 · 不可推测" /></el-form-item>
        </div>
        <el-button type="primary" :loading="saving" @click="submit">{{ editingId ? '保存为新版本' : '创建版本 1' }}</el-button>
        <el-button v-if="editingId" @click="reset">取消编辑</el-button>
      </el-form>
    </el-card>
    <el-card class="list" shadow="never">
      <template #header>
        <span>本租户产品</span>
        <el-button text type="primary" @click="load">刷新</el-button>
      </template>
      <el-table v-loading="loading" :data="products" empty-text="还没有产品、方案或案例。">
        <el-table-column prop="id" label="产品" width="90">
          <template #default="s">#{{ s.row.id }}</template>
        </el-table-column>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="currentVersionId" label="当前版本 ID" width="120" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="s">
            <el-button text type="primary" @click="edit(s.row.id)">维护事实</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card v-if="detail" class="list" shadow="never">
      <template #header>当前事实展示（空值不会被补写）</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="版本">{{ detail.currentVersion || '未录入 · 不可推测' }}</el-descriptions-item>
        <el-descriptions-item label="公开范围">{{ detail.publicScope || '未录入 · 不可推测' }}</el-descriptions-item>
        <el-descriptions-item v-for="(value, key) in detail.factDisplay" :key="key" :label="String(key)">
          {{ value }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </section>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createProduct, getProduct, getProductList, saveProductVersion, type ProductAsset, type ProductDetail } from '@/api/marketing/asset'

defineOptions({ name: 'TenantAssets' })

const loading = ref(false)
const saving = ref(false)
const editingId = ref<number>()
const products = ref<ProductAsset[]>([])
const detail = ref<ProductDetail>()
const form = reactive({ name: '', publicScope: 'restricted', moq: '', leadTime: '', price: '', certification: '' })

const factsJson = () =>
  JSON.stringify({ moq: form.moq, leadTime: form.leadTime, price: form.price, certification: form.certification })

const reset = () => {
  editingId.value = undefined
  detail.value = undefined
  form.name = ''
  form.publicScope = 'restricted'
  form.moq = ''
  form.leadTime = ''
  form.price = ''
  form.certification = ''
}

const load = async () => {
  loading.value = true
  try {
    products.value = await getProductList()
  } finally {
    loading.value = false
  }
}

const edit = async (id: number) => {
  const current = await getProduct(id)
  editingId.value = id
  detail.value = current
  form.name = current.name
  form.publicScope = current.publicScope || 'restricted'
  const facts = current.facts ? JSON.parse(current.facts) : {}
  form.moq = facts.moq || ''
  form.leadTime = facts.leadTime || ''
  form.price = facts.price || ''
  form.certification = facts.certification || ''
}

const submit = async () => {
  if (!form.name.trim()) return ElMessage.warning('请填写产品名称')
  saving.value = true
  try {
    const payload = { name: form.name, publicScope: form.publicScope, facts: factsJson() }
    if (editingId.value) {
      await saveProductVersion(editingId.value, payload)
      ElMessage.success('已保存新版本，空字段仍为未录入')
      await edit(editingId.value)
    } else {
      await createProduct(payload)
      ElMessage.success('已创建产品版本 1')
      reset()
    }
    await load()
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
<style scoped>
.assets {
  max-width: 1060px;
  padding: 8px 8px 32px;
}
.assets header {
  margin-bottom: 18px;
}
.assets h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.assets p {
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
