import request from '@/config/axios'

export interface ContentPackage {
  id: number
  taskId: number
  channel: string
  status: string
  currentVersionId?: number
  createTime: string
}

export const getContentPackageList = () => request.get<ContentPackage[]>({ url: '/marketing/content-package/list' })
export const createContentPackage = (data: { title: string; market: string; channel: string; content: string; factSnapshot?: string }) =>
  request.post<{ contentVersionId: number; contentHash: string }>({ url: '/marketing/content-package', data })
