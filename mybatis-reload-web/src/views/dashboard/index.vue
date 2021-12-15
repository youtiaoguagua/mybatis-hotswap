<template>
  <div class="dashboard-container">
    <div class="dashboard-bg"></div>
    
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    width: 100%;
    height: 100%;
    position: absolute;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }

  &-bg{
    background-image: url("https://ae03.alicdn.com/kf/Hae5d232034904e47a7d38c3add5ff6a8P.png");
    height: 100%;
    background-size: cover;
    background-repeat: no-repeat;
  }
}
</style>
