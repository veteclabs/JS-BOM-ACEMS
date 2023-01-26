<template>
  <div>
    <div class="title-box">
      <h2>
        {{ title }}
      </h2>
    </div>
    <div class="row dashboard-item-box">
      <div v-for="item in propsdata" :key="item.id" class="col-lg-3">

        <div class="ibox">
          <div class="ibox-title">
            <h3>{{ item.name }}</h3>
          </div>
          <ul class="state-box">
            <li v-if="item.state.COMP_Power !== undefined"
                :class="{'active' : item.state.COMP_Power.value === 1, 'set-btn run': true}"
                @click="setAirCompressor(item.id, 1)">RUN
            </li>
            <li v-if="item.state.COMP_Power !== undefined"
                :class="{'active' : item.state.COMP_Power.value === 0, 'set-btn stop': true}"
                @click="setAirCompressor(item.id, 0)">STOP
            </li>
          </ul>
          <div class="ibox-content" v-if="item.tagByComponents.mainInfoComponent.length !== 0">

            <ul class="tag-box">
              <li v-for="tag in item.tagByComponents.mainInfoComponent" :key="tag.id">
                <div class="tagname">{{ tag.description }}</div>
                <div>
                  {{ tag.value| valueFormat(2) }}{{ tag.unit }}
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  props: ['propsdata', 'title'],

  filters: {
    valueFormat: (value, numFix) => {
      value = parseFloat(value);
      if (!value) {
        return '0';
      } else {
        return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
      }
    },
  },
  data() {
    return {
      msgData: {
        // 알람모달
        msg: '',
        show: false,
        e: '',
      },
      loadingData: {
        show: false,
      },
      powerState: ['STOP', 'RUN']
    }
  },
  methods: {
    async setAirCompressor(id, stateValue) {
      const vm = this;

      const confirmResult = confirm(`해당 컴프레셔 상태를 ${this.powerState[stateValue]}으로 변경합니다. 수동제어 명령 진행 시 스케줄 제어는 모두 해제 됩니다.진행하시겠습니까?`);
      if (confirmResult) {
        vm.loadingData.show = true;
        axios.put(`/api/airDryer/${id}/power/${stateValue}`)
            .then((res) => {
              vm.msgData.show = true;
              vm.msgData.msg = res.data.message;
            }).catch((error) => {
          vm.msgData.show = true;
          vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
        }).finally(() => {
          vm.loadingData.show = false;
        });
      } else {
        vm.msgData.show = true;
        vm.msgData.msg = '제어명령이 취소되었습니다';
      }
      return true;
    },
  },
};
</script>
