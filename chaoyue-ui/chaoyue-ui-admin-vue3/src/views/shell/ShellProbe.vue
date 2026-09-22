<template>
  <div class="shell-probe">
    <p v-if="loading">正在探测身份…</p>
    <p v-else-if="error" class="shell-probe__err">{{ error }}</p>
    <template v-else>
      <p>
        ping：{{ ping?.ok ? '通' : '失败' }} · 用户 {{ ping?.userId }} · 类型
        {{ ping?.userType === 2 ? 'ADMIN' : ping?.userType }} · 租户 {{ ping?.tenantId }}
      </p>
      <p>本租户可见用户：{{ userLine }}</p>
      <p v-if="checkPlatform">平台写接口：{{ platformWrite }}</p>
      <p class="shell-probe__hint">跨租户验收：租户 B 不应出现 oper / reviewer。</p>
    </template>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref, computed } from 'vue'
import { pingMarketing, probePlatformWrite, type MarketingPingVO } from '@/api/marketing/ping'
import { getUserPage } from '@/api/system/user'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'ShellProbe' })

const props = defineProps<{ checkPlatform?: boolean }>()
const userStore = useUserStore()

const loading = ref(true)
const error = ref('')
const ping = ref<MarketingPingVO>()
const users = ref<{ username?: string }[]>([])
const platformWrite = ref('')
const userHint = ref('')

const names = computed(() =>
  users.value
    .map((u) => u.username)
    .filter(Boolean)
    .join('、')
)
const userLine = computed(() => names.value || userHint.value || '（无）')

onMounted(async () => {
  try {
    ping.value = await pingMarketing()
    if (userStore.getPermissions.has('system:user:query')) {
      const page = await getUserPage({ pageNo: 1, pageSize: 20 })
      users.value = (page as any)?.list || []
    } else {
      userHint.value = '当前角色无用户列表权限'
    }
    if (props.checkPlatform) {
      try {
        await probePlatformWrite()
        platformWrite.value = '允许'
      } catch {
        platformWrite.value = '拒绝（403）'
      }
    }
  } catch (e: any) {
    error.value = e?.message || '探测失败，请确认已登录且后端 48080 在跑'
  } finally {
    loading.value = false
  }
})
</script>
<style scoped>
.shell-probe {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  font-size: 14px;
  line-height: 1.5;
  color: #1f2937;
}
.shell-probe__err {
  color: #dc2626;
}
.shell-probe__hint {
  color: #6b7280;
  font-size: 12px;
}
</style>
