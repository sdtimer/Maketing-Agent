import request from '@/config/axios'
import { marketingAppUrl } from '@/api/marketing/app'

export interface PublishedEntry {
  entryId: number
  sortOrder: number
  recommendText: string
  reason: string
}

export interface PublishedBatch {
  batchId: number
  publishedAt: string
  opsNote: string
  entries: PublishedEntry[]
  disclaimer: string
}

export const getPublishedCatalog = (market: string, channel: string) =>
  request.get<PublishedBatch[]>({
    url: marketingAppUrl('/catalog/published'),
    params: { market, channel }
  })
