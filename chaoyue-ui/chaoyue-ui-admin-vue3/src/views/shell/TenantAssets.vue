<template>
  <section class="assets">
    <header>
      <div>
        <h1>组织与业务资产</h1>
        <p>参数允许为空。空值显示为未录入，不会由模型补写。</p>
      </div>
    </header>
    <el-card shadow="never">
      <template #header>新增产品</template>
      <el-form :model="form" label-position="top">
        <div class="grid">
          <el-form-item label="产品名称"><el-input v-model="form.name" maxlength="256" /></el-form-item>
          <el-form-item label="公开范围">
            <el-select v-model="form.publicScope">
              <el-option label="受限" value="restricted" />
              <el-option label="内部" value="internal" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="事实快照 JSON（可空）">
          <el-input v-model="form.facts" type="textarea" :rows="4" placeholder='{"moq":"","leadTime":""}' />
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="create">保存产品版本 1</el-button>
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
        <el-table-column prop="currentVersionId" label="当前版本" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
    </el-card>
  </section>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createProduct, getProductList, type ProductAsset } from '@/api/marketing/asset'

defineOptions({ name: 'TenantAssets' })

const loading = ref(false)
const saving = ref(false)
const products = ref<ProductAsset[]>([])
const form = reactive({ name: '', facts: '{}', publicScope: 'restricted' })

const load = async () => {
  loading.value = true
  try {
    products.value = await getProductList()
  } finally {
    loading.value = false
  }
}

const create = async () => {
  if (!form.name.trim()) return ElMessage.warning('请填写产品名称')
  saving.value = true
  try {
    await createProduct({ ...form })
    ElMessage.success('已创建产品版本 1，空字段保持未录入')
    form.name = ''
    form.facts = '{}'
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
