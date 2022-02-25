<template>
    <div>
        <span :class="`bom-badge ${colorType}`">{{ cellData.text }}%</span>
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
        colorType: '',
      };
    },
    mounted() {
      if(this.cellData.value <0) {
        this.colorType = 'red-badge red'
      } else if( this.cellData.value === 0) {
        this.colorType ='';
      } else {
        this.colorType = 'green-badge green'
      }
    },
  };
</script>
