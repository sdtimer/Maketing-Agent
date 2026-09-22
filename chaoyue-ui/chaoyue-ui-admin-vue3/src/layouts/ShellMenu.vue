<template>
  <div v-for="item in rows" :key="item.key" class="shell-node">
    <button
      v-if="item.children.length"
      type="button"
      class="shell-node__toggle"
      :class="{ 'is-current': childActive(item) }"
      :aria-expanded="isOpen(item.key)"
      @click="toggle(item.key)"
    >
      <span>{{ item.title }}</span>
      <i class="shell-node__chev" :class="{ 'is-open': isOpen(item.key) }" aria-hidden="true"></i>
    </button>
    <router-link
      v-else
      :to="item.path"
      class="product-shell__link"
      :class="{ 'is-active': active(item.path) }"
    >
      {{ item.title }}
    </router-link>
    <div v-if="item.children.length && isOpen(item.key)" class="shell-node__sub">
      <router-link
        v-for="child in item.children"
        :key="child.key"
        :to="child.path"
        class="shell-node__sublink"
        :class="{ 'is-active': active(child.path) }"
      >
        {{ child.title }}
      </router-link>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

defineOptions({ name: 'ShellMenu' })

const props = defineProps<{
  nodes: AppRouteRecordRaw[]
  parentPath: string
}>()

const route = useRoute()
const openKeys = ref<string[]>([])

const join = (parent: string, path: string) => {
  if (!path) return parent
  if (path.startsWith('/')) return path
  return `${parent}/${path}`.replace(/\/+/g, '/')
}

type Row = { key: string; title: string; path: string; children: { key: string; title: string; path: string }[] }

const rows = computed<Row[]>(() =>
  (props.nodes || [])
    .filter((item) => !item.meta?.hidden)
    .map((item) => {
      const path = join(props.parentPath, item.path)
      const title = String(item.meta?.title || item.name || path)
      const children = (item.children || [])
        .filter((child) => !child.meta?.hidden)
        .map((child) => {
          const childPath = join(path, child.path)
          return {
            key: String(child.name || childPath),
            title: String(child.meta?.title || child.name || childPath),
            path: childPath
          }
        })
      return { key: String(item.name || path), title, path, children }
    })
)

const active = (path: string) => route.path === path || route.path.startsWith(path + '/')
const childActive = (item: Row) => item.children.some((child) => active(child.path))
const isOpen = (key: string) => openKeys.value.includes(key)
const toggle = (key: string) => {
  openKeys.value = isOpen(key) ? openKeys.value.filter((item) => item !== key) : [...openKeys.value, key]
}

watch(
  [() => route.path, rows],
  () => {
    const next = new Set(openKeys.value)
    for (const item of rows.value) {
      if (childActive(item)) next.add(item.key)
    }
    openKeys.value = [...next]
  },
  { immediate: true }
)
</script>
