<template>
    <div>
        {{ cellData.text }}{{unitTemplate[cellData.column.dataField]}}
    </div>
</template>

<script>
    export default {
        props: {
            cellData: {
                type: Object,
                default: () => {}
            }
        },
      data() {
          return{
            unitType:'',
            unitTemplate: {
              purpose: '㎡',
              site_area:'㎡',
              building_area:'㎡',
              total_floor_area:'㎡',
              resident_staff_cnt:'명',
              floor_area:'㎡'
            }
          }
      },
      mounted() {
          this.unitType = this.cellData.column.dataField;
      }
    };
</script>
