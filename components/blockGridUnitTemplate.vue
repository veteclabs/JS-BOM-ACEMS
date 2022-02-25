<template>
    <div>
        <span class="bom-badge">{{ unitText }}</span>
    </div>
</template>

<script>
  export default {
    props: {
      cellData: {
        type: Object,
        default: () => {
        }
      },
    },
    data() {
      return {
        unitText: '',
      };
    },
    mounted() {
      if (typeof this.cellData.data.unit !== 'undefined') {
        this.unitText = this.cellData.value + this.cellData.data.unit;
      }else {
        this.unitText = this.cellData.value;
      }
    },
  };
</script>
