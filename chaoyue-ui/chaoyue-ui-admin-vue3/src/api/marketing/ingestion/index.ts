import request from '@/config/axios'

export interface ManualIngestionReqVO {
  market: string
  channel: string
  recordType: string
  subjectKey: string
  title?: string
  content?: string
  idempotencyKey: string
}

export interface UrlIngestionReqVO {
  market: string
  channel: string
  sourceUrl: string
  idempotencyKey: string
}

export interface ImageIngestionReqVO {
  market: string
  channel: string
  objectKey: string
  idempotencyKey: string
}

export interface IngestionTaskVO {
  taskId: number
  status: string
}

export interface IngestionFieldChangeVO {
  fieldName: string
  oldValue?: string
  newValue?: string
  creator?: string
  createTime: string
}

export interface IngestionDraftVO {
  taskId: number
  method: string
  market: string
  channel: string
  status: string
  version: number
  fields: Record<string, any>
  fieldChanges?: IngestionFieldChangeVO[]
}

export interface PlatformReviewRecordVO {
  id: number
  targetVersionId: number
  decision: string
  reasonCode?: string
  comment?: string
  reviewerId: number
  createTime: string
}

export interface IngestionTaskPageItem {
  id: number
  method: string
  market: string
  channel: string
  status: string
  attempt: number
  version: number
  createTime: string
}

/** 平台手工收录：只生成待校对草稿，绝不直接发布。 */
export const createManualIngestion = (data: ManualIngestionReqVO) => {
  return request.post<IngestionTaskVO>({ url: '/marketing/ingestion/manual', data })
}

/** 仅登记一个公开页面，不接收链接数组。 */
export const createUrlIngestion = (data: UrlIngestionReqVO) => {
  return request.post<IngestionTaskVO>({ url: '/marketing/ingestion/parse-url', data })
}

/** 上传后的文件引用进入受控识别任务。 */
export const createImageIngestion = (data: ImageIngestionReqVO) => {
  return request.post<IngestionTaskVO>({ url: '/marketing/ingestion/recognize-image', data })
}

export const getIngestionTaskPage = (params: PageParam & { status?: string; channel?: string }) => {
  return request.get<PageResult<IngestionTaskPageItem[]>>({ url: '/marketing/ingestion/page', params })
}

export const getIngestionDraft = (id: number) => request.get<IngestionDraftVO>({ url: `/marketing/ingestion/${id}` })
export const updateIngestionDraft = (id: number, data: { expectedVersionId: number; fields: Record<string, any> }) =>
  request.put<IngestionDraftVO>({ url: `/marketing/ingestion/${id}/draft`, data })
export const submitIngestionReview = (id: number) => request.post<boolean>({ url: `/marketing/ingestion/${id}/submit-review` })
export const approvePlatformReview = (id: number, expectedVersionId: number) =>
  request.post<boolean>({ url: `/marketing/platform/review/${id}/approve`, data: { expectedVersionId } })
export const rejectPlatformReview = (id: number, expectedVersionId: number, reasonCode: string, comment: string) =>
  request.post<boolean>({ url: `/marketing/platform/review/${id}/reject`, data: { expectedVersionId, reasonCode, comment } })
export const getPlatformReviewRecords = (id: number) =>
  request.get<PlatformReviewRecordVO[]>({ url: `/marketing/platform/review/${id}` })
