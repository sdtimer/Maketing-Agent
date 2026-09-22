import request from '@/config/axios'
import { marketingAppUrl } from '@/api/marketing/app'

export interface ProductAsset {
  id: number
  name: string
  status: string
  currentVersionId?: number
  createTime: string
}

export const getProductList = () => request.get<ProductAsset[]>({ url: marketingAppUrl('/catalog/product') })
export const createProduct = (data: { name: string; facts?: string; publicScope?: string }) =>
  request.post<number>({ url: marketingAppUrl('/catalog/product'), data })
