/** 00 总册 §2：平台角色 → /admin，租户角色 → /tenant。前端守卫只做体验。 */

const PLATFORM_USERS = new Set(['admin', 'platform'])

export function isPlatformUser(username?: string, roles: string[] = []) {
  // admin 的平台权限来自 super_admin；以角色为准可避免用户信息缓存尚未刷新时误跳 403。
  return PLATFORM_USERS.has(username || '') || roles.includes('platform_ops') || roles.includes('super_admin')
}

export function isSystemAdmin(username?: string) {
  return username === 'admin'
}

export function shellHome(username?: string, roles: string[] = []) {
  return isPlatformUser(username, roles) ? '/admin/home' : '/tenant/home'
}

function isOpen(path: string) {
  return (
    path.startsWith('/login') ||
    path.startsWith('/403') ||
    path.startsWith('/404') ||
    path.startsWith('/500') ||
    path.startsWith('/user') ||
    path.startsWith('/redirect') ||
    path.startsWith('/sso') ||
    path.startsWith('/social-login')
  )
}

/** 返回应跳转路径；null 表示放行 */
export function guardShell(path: string, username?: string, roles: string[] = []): string | null {
  if (isOpen(path)) return null
  const platform = isPlatformUser(username, roles)
  if (path === '/' || path === '/index') return shellHome(username, roles)
  if (path.startsWith('/tenant')) return platform ? '/403' : null
  if (path.startsWith('/admin')) return platform ? null : '/403'
  if (isSystemAdmin(username)) return null
  return '/403'
}
