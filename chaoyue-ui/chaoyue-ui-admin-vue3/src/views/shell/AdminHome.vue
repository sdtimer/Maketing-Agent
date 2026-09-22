<template>
  <div class="shell-empty">
    <h1>平台工作台</h1>
    <p>这个渠道还没有精选批次。</p>
    <p>下一步：请平台运营收录（M1 上线后从这里进入）。</p>
    <ShellProbe check-platform />
  </div>
</template>
<script lang="ts" setup>
import ShellProbe from './ShellProbe.vue'

defineOptions({ name: 'AdminHome' })
</script>
