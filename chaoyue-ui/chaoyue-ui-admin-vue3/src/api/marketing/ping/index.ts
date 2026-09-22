import request from '@/config/axios'

export interface MarketingPingVO {
  ok: boolean
  userId: number
  userType: number
  tenantId: number
}

/** M0 探测：/app-api/marketing/** 必须按 ADMIN 鉴权 */
export const pingMarketing = () => {
  const base = import.meta.env.VITE_BASE_URL as string
  return request.get<MarketingPingVO>({
    url: `${base}/app-api/marketing/ping`
  })
}

/** 平台写探针。租户 Token 必须 403。 */
export const probePlatformWrite = () => {
  return request.post<boolean>({ url: '/marketing/platform/probe' })
}
