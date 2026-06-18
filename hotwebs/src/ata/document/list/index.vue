<template>
  <div id="app">
    <ListComponent />
  </div>
</template>

<script>
import ListComponent from './index.js';
export default {
    name: 'AtaDocumentList',
    components: {
        ListComponent
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
