<template>
  <div id="app">
    <CardComponent />
  </div>
</template>

<script>
import CardComponent from './index.js';
export default {
    name: 'AtaDocumentCard',
    components: {
        CardComponent
    },
    data() {
        return {};
    }
};
</script>

<style lang="less" scoped>
#app {
  width: 100%;
  height: 100%;
}
</style>
