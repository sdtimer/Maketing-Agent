<template>
  <div class="shell-empty">
    <h1>租户工作台</h1>
    <p>这个租户还没有内容资产。</p>
    <p>下一步：去维护业务资产（M1 收录上线后从这里进入）。</p>
    <ShellProbe />
  </div>
</template>
<script lang="ts" setup>
import ShellProbe from './ShellProbe.vue'

defineOptions({ name: 'TenantHome' })
</script>
