<template>
  <span class="logo">
    <el-image
      style="width:80px; height: 80px"
      src="/mainIcon.png"
      fit="fill"
    ></el-image>
    <div v-if="!flag">
      <span :style="{ color: bag, display: 'block' }">{{ name }}</span>
    </div>
  </span>
</template>
<script>
export default {
  name: "Logo",
  data() {
    return {};
  },
  props: {
    name: {
      type: String,

      default: "System",
    },
    flag: {
      type: Boolean,
      required: false,
    },
    bag: {
      type: String,
      default: "rgb(255,255,255)",
    },
  },
  created() {},
  methods: {},
};
</script>
<style scoped lang="scss">
.logo {
  color: rgb(8, 24, 16) !important;
  font-size: 14px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  font-size: 20px;
  user-select: none;

  span {
    margin-left: 0px;
    font-family: "Times New Roman", Times, serif;
  }
}
</style>