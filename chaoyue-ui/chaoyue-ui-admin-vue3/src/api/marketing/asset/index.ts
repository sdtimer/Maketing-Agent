import request from '@/config/axios'
import { marketingAppUrl } from '@/api/marketing/app'

export interface ProductAsset {
  id: number
  name: string
  status: string
  currentVersionId?: number
  createTime: string
}

export interface ProductDetail extends ProductAsset {
  currentVersion?: number
  publicScope?: string
  facts?: string
  factDisplay?: Record<string, string>
}

export interface AuthorizedAsset {
  id: number
  name: string
  objectKey: string
  source?: string
  usageScope?: string
  authorized: boolean
  createTime: string
}

export const getProductList = () => request.get<ProductAsset[]>({ url: marketingAppUrl('/catalog/product') })
export const getProduct = (id: number) => request.get<ProductDetail>({ url: marketingAppUrl(`/catalog/product/${id}`) })
export const createProduct = (data: { name: string; facts?: string; publicScope?: string }) =>
  request.post<number>({ url: marketingAppUrl('/catalog/product'), data })
export const saveProductVersion = (id: number, data: { name: string; facts?: string; publicScope?: string }) =>
  request.put<number>({ url: marketingAppUrl(`/catalog/product/${id}/version`), data })
export const getAuthorizedAssets = () => request.get<AuthorizedAsset[]>({ url: marketingAppUrl('/catalog/library') })
export const createAuthorizedAsset = (data: {
  name: string
  objectKey: string
  source?: string
  usageScope?: string
  authorized: boolean
}) => request.post<number>({ url: marketingAppUrl('/catalog/library'), data })
