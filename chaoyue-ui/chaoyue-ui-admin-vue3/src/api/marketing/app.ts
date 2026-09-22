/** 租户营销接口走 /app-api/marketing/**，由后端映射为 ADMIN 鉴权。 */
export const marketingAppUrl = (path: string) =>
  `${import.meta.env.VITE_BASE_URL}/app-api/marketing${path}`
