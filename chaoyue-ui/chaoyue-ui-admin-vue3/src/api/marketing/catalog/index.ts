import request from '@/config/axios'

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

export const getPublishedCatalog = (market: string, channel: string) => {
  const base = import.meta.env.VITE_BASE_URL as string
  return request.get<PublishedBatch[]>({
    url: `${base}/app-api/marketing/catalog/published`,
    params: { market, channel }
  })
}
