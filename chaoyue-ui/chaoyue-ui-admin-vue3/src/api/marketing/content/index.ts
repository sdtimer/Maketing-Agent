import request from '@/config/axios'
import { marketingAppUrl } from '@/api/marketing/app'

export interface ContentPackage {
  id: number
  taskId: number
  channel: string
  status: string
  currentVersionId?: number
  createTime: string
}

export interface ContentPackageDetail extends ContentPackage {
  currentVersion?: number
  contentHash?: string
  content?: string
}

export interface GateResult {
  canApprove: boolean
  canCopy: boolean
  canExport: boolean
  reason: string
  contentVersionId?: number
  contentHash?: string
}

export interface ContentVersionItem {
  id: number
  version: number
  contentHash: string
  createTime: string
  current: boolean
}

export interface ContentReviewRecord {
  id: number
  contentVersionId: number
  decision: string
  comment?: string
  reviewerId: number
  createTime: string
}

export const getContentPackageList = () =>
  request.get<ContentPackage[]>({ url: marketingAppUrl('/content-package/list') })
export const getContentPackage = (id: number) =>
  request.get<ContentPackageDetail>({ url: marketingAppUrl(`/content-package/${id}`) })
export const createContentPackage = (data: {
  title: string
  market: string
  channel: string
  content: string
  factSnapshot?: string
}) => request.post<{ contentVersionId: number; contentHash: string }>({ url: marketingAppUrl('/content-package'), data })
export const saveContentVersion = (id: number, content: string) =>
  request.put<{ contentVersionId: number; contentHash: string }>({
    url: marketingAppUrl(`/content-package/${id}/content`),
    data: { content }
  })
export const approveContentPackage = (id: number, versionId: number) =>
  request.post<boolean>({ url: marketingAppUrl(`/content-package/${id}/approve`), data: { versionId } })
export const rejectContentPackage = (id: number, versionId: number, comment: string) =>
  request.post<boolean>({ url: marketingAppUrl(`/content-package/${id}/reject`), data: { versionId, comment } })
export const copyContentPackage = (id: number) =>
  request.post<{ contentVersionId: number; contentHash: string; content: string }>({
    url: marketingAppUrl(`/content-package/${id}/copy`)
  })
export const exportContentPackageHtml = (id: number) =>
  request.post<string>({ url: marketingAppUrl(`/content-package/${id}/export/html`) })
export const getContentGate = (id: number) =>
  request.get<GateResult>({ url: marketingAppUrl(`/gate/content-package/${id}`) })
export const getContentVersions = (id: number) =>
  request.get<ContentVersionItem[]>({ url: marketingAppUrl(`/content-package/${id}/versions`) })
export const getContentReviews = (id: number) =>
  request.get<ContentReviewRecord[]>({ url: marketingAppUrl(`/content-package/${id}/reviews`) })
