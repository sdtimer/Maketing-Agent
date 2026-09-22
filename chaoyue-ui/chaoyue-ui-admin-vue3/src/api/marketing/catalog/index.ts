import request from '@/config/axios'
export interface PublishedEntry { taskId: number; recommendText: string; reason: string }
export interface PublishedBatch { batchId: number; publishedAt: string; opsNote: string; entries: PublishedEntry[]; disclaimer: string }
export const getPublishedCatalog = (market: string, channel: string) => request.get<PublishedBatch[]>({ url: '/app-api/marketing/catalog/published', params: { market, channel } })
