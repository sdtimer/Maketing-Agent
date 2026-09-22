<template>
  <section class="ingest-page">
    <header class="ingest-page__header">
      <div><h1>收录工作台</h1><p>三种录入方式进入同一套校对、审核与发布流程。</p></div>
      <el-tag type="warning" effect="plain">平台内容运营</el-tag>
    </header>

    <el-tabs v-model="activeTab" class="ingest-page__tabs">
      <el-tab-pane label="手工录入" name="manual">
        <el-card shadow="never" class="ingest-card">
          <template #header><div class="ingest-card__title"><span>手工录入</span><small>填写后只会创建待校对草稿</small></div></template>
          <el-form ref="manualFormRef" :model="manualForm" :rules="manualRules" label-position="top" class="ingest-form">
            <div class="ingest-form__grid">
              <el-form-item label="市场范围"><el-select v-model="manualForm.market"><el-option label="国内" value="domestic" /><el-option label="出海" value="overseas" /></el-select></el-form-item>
              <el-form-item label="渠道"><el-select v-model="manualForm.channel"><el-option v-for="item in channels" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
              <el-form-item label="记录类型" prop="recordType"><el-radio-group v-model="manualForm.recordType"><el-radio-button label="account">对标号</el-radio-button><el-radio-button label="content">内容</el-radio-button></el-radio-group></el-form-item>
              <el-form-item :label="manualForm.recordType === 'account' ? '账号唯一标识' : '内容唯一定位'" prop="subjectKey"><el-input v-model="manualForm.subjectKey" :placeholder="manualForm.recordType === 'account' ? '如公众号名称或账号 ID' : '如原标题、链接定位'" /></el-form-item>
              <el-form-item label="标题或账号名称"><el-input v-model="manualForm.title" maxlength="256" show-word-limit /></el-form-item>
              <el-form-item class="ingest-form__full" label="录入内容 / 简介 / 入选理由"><el-input v-model="manualForm.content" type="textarea" :rows="4" maxlength="20000" show-word-limit /></el-form-item>
            </div>
            <div class="ingest-form__actions"><el-button @click="resetManual">重置</el-button><el-button type="primary" :loading="saving" @click="submitManual">提交待校对</el-button></div>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="指定页面 AI 解析" name="url">
        <el-card shadow="never" class="ingest-card">
          <template #header><div class="ingest-card__title"><span>指定页面 AI 解析</span><small>只接受一个公开页面地址</small></div></template>
          <el-alert class="ingest-alert" type="warning" :closable="false" show-icon><template #title>指定一个公开页面，系统只解析这一页。不支持登录页、验证码页、批量链接。</template></el-alert>
          <el-form ref="urlFormRef" :model="urlForm" :rules="urlRules" label-position="top" class="ingest-form">
            <div class="ingest-form__grid">
              <el-form-item label="市场范围"><el-select v-model="urlForm.market"><el-option label="国内" value="domestic" /><el-option label="出海" value="overseas" /></el-select></el-form-item>
              <el-form-item label="渠道"><el-select v-model="urlForm.channel"><el-option v-for="item in channels" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
              <el-form-item class="ingest-form__full" label="公开页面地址" prop="sourceUrl"><el-input v-model="urlForm.sourceUrl" placeholder="https://example.com/a-public-page" clearable /></el-form-item>
            </div>
            <div class="ingest-form__actions"><el-button @click="resetUrl">重置</el-button><el-button type="primary" :loading="saving" @click="submitUrl">提交待校对</el-button></div>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="上传图片 AI 识别" name="image">
        <el-card shadow="never" class="ingest-card">
          <template #header><div class="ingest-card__title"><span>上传图片 AI 识别</span><small>图片将进入受控识别任务</small></div></template>
          <el-alert class="ingest-alert" type="warning" :closable="false" show-icon><template #title>识别结果是待校对草稿，用户端现在看不到。</template></el-alert>
          <el-form ref="imageFormRef" :model="imageForm" :rules="imageRules" label-position="top" class="ingest-form">
            <div class="ingest-form__grid">
              <el-form-item label="市场范围"><el-select v-model="imageForm.market"><el-option label="国内" value="domestic" /><el-option label="出海" value="overseas" /></el-select></el-form-item>
              <el-form-item label="渠道"><el-select v-model="imageForm.channel"><el-option v-for="item in channels" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
              <el-form-item class="ingest-form__full" label="截图或图片" prop="objectKey">
                <el-upload v-model:file-list="imageFiles" accept="image/png,image/jpeg,image/webp" :limit="1" :http-request="uploadImage" :before-upload="validateImage" :on-remove="clearImage">
                  <el-button :loading="uploading" plain>选择图片</el-button>
                  <template #tip><div class="el-upload__tip">PNG、JPG、WebP，单张不超过 10 MB。</div></template>
                </el-upload>
              </el-form-item>
            </div>
            <div class="ingest-form__actions"><el-button @click="resetImage">重置</el-button><el-button type="primary" :loading="saving" @click="submitImage">提交待校对</el-button></div>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-alert v-if="createdTask" class="ingest-page__result" type="success" :closable="false" show-icon>已创建待校对草稿：任务 #{{ createdTask.taskId }}（{{ createdTask.status }}）。尚未审核，未对租户端发布。</el-alert>

    <el-card shadow="never" class="ingest-task-card">
      <template #header><div class="ingest-card__title"><span>最近收录任务</span><small>平台域草稿，租户端不可见</small><el-button text type="primary" :loading="taskLoading" @click="loadTasks">刷新</el-button></div></template>
      <el-table v-loading="taskLoading" :data="tasks" size="small" empty-text="还没有收录任务">
        <el-table-column prop="id" label="任务" width="90"><template #default="scope">#{{ scope.row.id }}</template></el-table-column>
        <el-table-column label="方式" width="130"><template #default="scope">{{ methodLabel(scope.row.method) }}</template></el-table-column>
        <el-table-column prop="market" label="市场" width="90"><template #default="scope">{{ scope.row.market === 'domestic' ? '国内' : '出海' }}</template></el-table-column>
        <el-table-column prop="channel" label="渠道" width="120"><template #default="scope">{{ channelLabel(scope.row.channel) }}</template></el-table-column>
        <el-table-column prop="status" label="状态" width="130"><template #default="scope"><el-tag size="small" :type="scope.row.status === 'pending_review' ? 'info' : 'warning'">{{ scope.row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="100"><template #default="scope"><el-button text type="primary" :disabled="scope.row.status !== 'parsed_draft'" @click="openDraft(scope.row.id)">校对</el-button></template></el-table-column>
      </el-table>
    </el-card>

    <el-drawer v-model="draftVisible" title="校对草稿" size="560px" destroy-on-close>
      <template v-if="draft">
        <el-alert type="warning" :closable="false" show-icon>字段为空即为空；校对不会自动补全，更不会自动发布。</el-alert>
        <el-descriptions :column="2" border class="draft-meta"><el-descriptions-item label="任务">#{{ draft.taskId }}</el-descriptions-item><el-descriptions-item label="版本">{{ draft.version }}</el-descriptions-item><el-descriptions-item label="方式">{{ methodLabel(draft.method) }}</el-descriptions-item><el-descriptions-item label="状态">{{ draft.status }}</el-descriptions-item></el-descriptions>
        <el-form label-position="top" class="draft-form">
          <el-form-item label="账号唯一标识 / 内容唯一定位（提交审核必填）"><el-input v-model="draft.fields.subjectKey" /></el-form-item>
          <el-form-item label="标题或账号名称"><el-input v-model="draft.fields.title" /></el-form-item>
          <el-form-item label="录入内容 / 简介 / 入选理由"><el-input v-model="draft.fields.content" type="textarea" :rows="7" /></el-form-item>
        </el-form>
        <div class="draft-actions"><el-button :loading="draftSaving" @click="saveDraft">保存校对</el-button><el-button type="primary" :loading="draftSubmitting" @click="submitReview">提交待审</el-button></div>
      </template>
    </el-drawer>
  </section>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadRequestOptions, type UploadUserFile } from 'element-plus'
import { updateFile } from '@/api/infra/file'
import { createImageIngestion, createManualIngestion, createUrlIngestion, getIngestionDraft, getIngestionTaskPage, submitIngestionReview, updateIngestionDraft, type ImageIngestionReqVO, type IngestionDraftVO, type IngestionTaskPageItem, type IngestionTaskVO, type ManualIngestionReqVO, type UrlIngestionReqVO } from '@/api/marketing/ingestion'

defineOptions({ name: 'AdminIngest' })

const channels = [{ label: '公众号', value: 'wechat' }, { label: '小红书', value: 'xiaohongshu' }, { label: '抖音', value: 'douyin' }, { label: '哔哩哔哩', value: 'bilibili' }, { label: 'LinkedIn', value: 'linkedin' }, { label: 'YouTube', value: 'youtube' }, { label: 'Facebook', value: 'facebook' }]
const activeTab = ref('manual')
const saving = ref(false)
const uploading = ref(false)
const createdTask = ref<IngestionTaskVO>()
const taskLoading = ref(false)
const tasks = ref<IngestionTaskPageItem[]>([])
const draftVisible = ref(false)
const draft = ref<IngestionDraftVO>()
const draftSaving = ref(false)
const draftSubmitting = ref(false)
const manualFormRef = ref<FormInstance>()
const urlFormRef = ref<FormInstance>()
const imageFormRef = ref<FormInstance>()
const imageFiles = ref<UploadUserFile[]>([])
const newKey = () => crypto.randomUUID().replaceAll('-', '')
const manualForm = reactive<ManualIngestionReqVO>({ market: 'domestic', channel: 'wechat', recordType: 'account', subjectKey: '', title: '', content: '', idempotencyKey: newKey() })
const urlForm = reactive<UrlIngestionReqVO>({ market: 'domestic', channel: 'wechat', sourceUrl: '', idempotencyKey: newKey() })
const imageForm = reactive<ImageIngestionReqVO>({ market: 'domestic', channel: 'wechat', objectKey: '', idempotencyKey: newKey() })
const manualRules: FormRules<ManualIngestionReqVO> = { subjectKey: [{ required: true, message: '请填写唯一标识或内容定位', trigger: 'blur' }] }
const urlRules: FormRules<UrlIngestionReqVO> = { sourceUrl: [{ required: true, message: '请输入一个公开页面地址', trigger: 'blur' }, { type: 'url', message: '请输入合法 URL', trigger: 'blur' }] }
const imageRules: FormRules<ImageIngestionReqVO> = { objectKey: [{ required: true, message: '请先上传一张图片', trigger: 'change' }] }
const validate = async (form: FormInstance | undefined) => Boolean(await form?.validate().catch(() => false))
const methodLabel = (method: string) => ({ manual: '手工录入', 'parse-url': '页面解析', 'recognize-image': '图片识别' })[method] || method
const channelLabel = (channel: string) => channels.find((item) => item.value === channel)?.label || channel
const loadTasks = async () => { taskLoading.value = true; try { tasks.value = (await getIngestionTaskPage({ pageNo: 1, pageSize: 20 })).list } finally { taskLoading.value = false } }
const openDraft = async (id: number) => { draft.value = await getIngestionDraft(id); draftVisible.value = true }
const saveDraft = async () => { if (!draft.value) return; draftSaving.value = true; try { draft.value = await updateIngestionDraft(draft.value.taskId, { expectedVersionId: draft.value.version, fields: draft.value.fields }); ElMessage.success('校对草稿已保存') } finally { draftSaving.value = false } }
const submitReview = async () => { if (!draft.value) return; draftSubmitting.value = true; try { await saveDraft(); await submitIngestionReview(draft.value.taskId); draftVisible.value = false; await loadTasks(); ElMessage.success('已提交待审，仍未发布') } finally { draftSubmitting.value = false } }
const complete = (task: IngestionTaskVO, resetKey: () => void) => { createdTask.value = task; resetKey(); void loadTasks(); ElMessage.success('已创建待校对草稿，尚未发布') }
const submitManual = async () => { if (!(await validate(manualFormRef.value))) return; saving.value = true; try { complete(await createManualIngestion({ ...manualForm }), () => (manualForm.idempotencyKey = newKey())) } finally { saving.value = false } }
const submitUrl = async () => { if (!(await validate(urlFormRef.value))) return; saving.value = true; try { complete(await createUrlIngestion({ ...urlForm }), () => (urlForm.idempotencyKey = newKey())) } finally { saving.value = false } }
const submitImage = async () => { if (!(await validate(imageFormRef.value))) return; saving.value = true; try { complete(await createImageIngestion({ ...imageForm }), () => (imageForm.idempotencyKey = newKey())) } finally { saving.value = false } }
const resetManual = () => { manualFormRef.value?.resetFields(); manualForm.title = ''; manualForm.content = ''; manualForm.idempotencyKey = newKey(); createdTask.value = undefined }
const resetUrl = () => { urlFormRef.value?.resetFields(); urlForm.idempotencyKey = newKey(); createdTask.value = undefined }
const clearImage = () => { imageForm.objectKey = '' }
const resetImage = () => { imageFormRef.value?.resetFields(); imageForm.idempotencyKey = newKey(); imageFiles.value = []; imageForm.objectKey = ''; createdTask.value = undefined }
const validateImage = (file: File) => { if (!['image/png', 'image/jpeg', 'image/webp'].includes(file.type) || file.size > 10 * 1024 * 1024) { ElMessage.error('请上传 10 MB 以内的 PNG、JPG 或 WebP 图片'); return false }; return true }
const uploadImage = async (options: UploadRequestOptions) => { uploading.value = true; try { imageForm.objectKey = await updateFile({ file: options.file, directory: 'marketing/ingestion' }); options.onSuccess?.(imageForm.objectKey); await imageFormRef.value?.validateField('objectKey') } catch (error) { options.onError?.(Object.assign(error instanceof Error ? error : new Error('上传失败'), { status: 0, method: 'POST', url: '/infra/file/upload' }) as never) } finally { uploading.value = false } }
onMounted(loadTasks)
</script>

<style scoped>
.ingest-page { max-width: 1060px; padding: 8px 8px 32px; }.draft-meta { margin: 18px 0; }.draft-form { margin-top: 18px; }.draft-actions { display: flex; justify-content: flex-end; gap: 10px; }.ingest-task-card { margin-top: 18px; }.ingest-card__title :deep(.el-button) { margin-left: auto; }.ingest-page__header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 18px; }.ingest-page__header h1 { margin: 0 0 7px; font-size: 22px; color: #1f2937; }.ingest-page__header p { margin: 0; color: #6b7280; font-size: 14px; }.ingest-page__tabs :deep(.el-tabs__header) { margin-bottom: 18px; }.ingest-card { border-color: #e5e7eb; }.ingest-card__title { display: flex; gap: 10px; align-items: baseline; font-weight: 650; color: #1f2937; }.ingest-card__title small { color: #6b7280; font-weight: 400; }.ingest-alert { margin-bottom: 18px; }.ingest-form__grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 0 18px; max-width: 820px; }.ingest-form__grid :deep(.el-select) { width: 100%; }.ingest-form__full { grid-column: 1 / -1; }.ingest-form__actions { display: flex; gap: 10px; padding-top: 4px; }.ingest-page__result { margin-top: 18px; }@media (max-width: 720px) { .ingest-form__grid { grid-template-columns: 1fr; }.ingest-form__full { grid-column: auto; } }
</style>
