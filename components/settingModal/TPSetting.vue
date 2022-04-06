<template>
    <div>
        <div class="ibox">
            <div class="ibox-title ibox-normal-title ibox-noborder-title">공기압축기 TP 제어</div>
            <table class="bom-table">
                <tr>
                    <td>
                        <div class="td-label">Min</div>
                        <label class="input-100">
                            <input type="number" v-model="barRange[0]" class="input-100" placeholder="최소압력"/>
                        </label>
                    </td>
                    <td>~</td>
                    <td>
                        <div class="td-label">Max</div>
                        <label class="input-100">
                            <input type="number" v-model="barRange[1]" class="input-100" placeholder="최대압력"/>
                        </label>
                    </td>
                </tr>
            </table>
            <VueSimpleRangeSlider id="bar-range" :min="0" :max="20" v-model="barRange"/>
        </div>
        <div class="ibox">
            <div class="ibox-title ibox-normal-title ibox-noborder-title">공기압축기 전체 스케줄 제어</div>
            <ul class="all-schedule-list">
                <li v-for="(week, property, index) in weekList" :class="property" :key="week.id">
                    <div class="td-label">{{weekName[index]}}</div>
                    <div v-if="week.length === 0 && index < 4" class="font-14">
                        스케줄 지정된 장비가 없습니다.
                    </div>
                    <draggable class="list-group" :list="week" group="people">
                        <div class="list-group-item equipment-box" v-for="(element, index) in week"
                             :key="element.name">
                            <div class="name-box">{{ element.name }}</div>
                            <ul class="date-ul">
                                <li v-for="item in dateList">
                                    <label>
                                        <input type="checkbox" :value="item.id" v-model="element.date"/>
                                        <div>{{item.name}}</div>
                                    </label>
                                </li>
                            </ul>
                        </div>
                    </draggable>
                </li>
            </ul>
        </div>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>
<script>
    import axios from 'axios';
    import VueSimpleRangeSlider from 'vue-simple-range-slider';
    import draggable from 'vuedraggable';
    import flashModal from '~/components/flashmodal.vue';

    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/');
            }
            return false;
        },
        layout: 'settingTemplate',
        components: {
            axios,
            VueSimpleRangeSlider,
            draggable,
            flashModal,
        },
        data() {
            return {
                id: '',
                msgData: {
                    msg: '',
                    show: false,
                    e: '',
                },
                barRange: [6, 8],
                weekName: ['첫째주', '둘째주', '셋째주', '넷째주', '스케줄 대기 장비'],
                weekList: {
                    week1:
                        [{
                            id: 1,
                            equipmentId: 'ingersollrand_rm55',
                            name: 'Ingersoll Rand RM55 -1',
                            scheduleWeek: 1,
                            date: [],
                            time: {start: '00:00', end: '00:00'}
                        }],
                    week2: [{
                        id: 1,
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -2',
                        scheduleWeek: 1,
                        date: [],
                        time: {start: '00:00', end: '00:00'}
                    }],
                    week3: [{
                        id: 1,
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -3',
                        scheduleWeek: 1,
                        date: [],
                        time: {start: '00:00', end: '00:00'}
                    }],
                    week4: [{
                        id: 1,
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -4',
                        scheduleWeek: 1,
                        date: [],
                        time: {start: '00:00', end: '00:00'}
                    }],
                    noWeek: [],
                },
                dateList: [
                    {id: 1, name: '월'},
                    {id: 2, name: '화'},
                    {id: 3, name: '수'},
                    {id: 4, name: '목'},
                    {id: 5, name: '금'},
                    {id: 6, name: '토'},
                    {id: 7, name: '일'}
                ],
                date: [],
                time: {
                    start: '00:00',
                    end: '00:00'
                },
                timeOptions: {
                    format: 'HH:mm',
                }
            };
        },
        methods: {
            async getTP() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/compressors',
                }).then((res) => {
                    vm.compressorList = res.data;
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            async submit() {
                this.msgData.show = true;
                this.msgData.msg = '저장이 완료되었습니다.'
            }
        },
    };
</script>
