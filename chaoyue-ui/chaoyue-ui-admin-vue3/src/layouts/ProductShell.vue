<template>
  <div class="product-shell" :class="mode === 'admin' ? 'shell-admin' : 'shell-tenant'">
    <aside class="product-shell__aside">
      <div class="product-shell__brand">
        <img
          :src="mode === 'admin' ? mark : full"
          alt="超悦"
          :class="mode === 'admin' ? 'product-shell__mark' : 'product-shell__full'"
        />
        <span v-if="mode === 'admin'" class="shell-badge">平台</span>
      </div>
      <nav class="product-shell__nav" aria-label="主导航">
        <ShellMenu :nodes="items" :parent-path="rootPath" />
        <div v-if="systemMenu && !systemMenu.meta?.hidden" class="product-shell__section">
          <button
            type="button"
            class="shell-node__toggle product-shell__section-toggle"
            :class="{ 'is-current': route.path.startsWith('/system') }"
            :aria-expanded="systemOpen"
            @click="systemOpen = !systemOpen"
          >
            <span>{{ systemMenu.meta?.title || '系统管理' }}</span>
            <i class="shell-node__chev" :class="{ 'is-open': systemOpen }" aria-hidden="true"></i>
          </button>
          <div v-show="systemOpen" class="shell-node__sub product-shell__system-sub">
            <ShellMenu :nodes="systemMenu.children || []" parent-path="/system" />
          </div>
        </div>
      </nav>
    </aside>
    <div class="product-shell__main">
      <header class="product-shell__top">
        <span class="product-shell__title">智造营销</span>
        <span class="product-shell__tag">内部验证</span>
        <span class="product-shell__spacer"></span>
        <span class="product-shell__who">{{ userStore.getUser.nickname || userStore.getUser.username }}</span>
        <button type="button" class="product-shell__out" @click="logout">退出</button>
      </header>
      <main class="product-shell__body">
        <router-view />
      </main>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePermissionStore } from '@/store/modules/permission'
import { useUserStore } from '@/store/modules/user'
import ShellMenu from './ShellMenu.vue'
import full from '@/assets/brand/logo-full.png'
import mark from '@/assets/brand/logo-mark.png'

const props = defineProps<{ mode: 'tenant' | 'admin' }>()
const route = useRoute()
const router = useRouter()
const systemOpen = ref(false)
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const rootPath = computed(() => (props.mode === 'admin' ? '/admin' : '/tenant'))
const root = computed(() => permissionStore.getRouters.find((item) => item.path === rootPath.value))
const items = computed(() => root.value?.children || [])
const systemMenu = computed(() =>
  props.mode === 'admin' ? permissionStore.getRouters.find((item) => item.path === '/system') : undefined
)

watch(
  () => route.path,
  (path) => {
    if (path.startsWith('/system')) systemOpen.value = true
  },
  { immediate: true }
)

const logout = async () => {
  await userStore.loginOut()
  router.replace('/login')
}
</script>
