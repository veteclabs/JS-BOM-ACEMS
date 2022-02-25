<template>
  <div>
    <b-modal v-model="propsdata.show" title="알림" ok-only auto-focus-button="ok">
      <div>
        {{ propsdata.msg }}
      </div>
    </b-modal>
  </div>
</template>
<script>
export default {
  props: ['propsdata'],
};
</script>
