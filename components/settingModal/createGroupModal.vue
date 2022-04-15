<template>
    <div>
        <b-modal v-model="propsdata.show" id="createGroupModal" title="그룹 관리" hide-footer>
            <table class="bom-table">
                <caption>그룹 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>그룹명</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="name" class="input-100"
                                   placeholder="그룹명을 입력해주세요 "/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('name') }}
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <h4 class="modal-h4-title">공기압축기 압력 제어 범위</h4>
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
            <h4 class="modal-h4-title">그룹 스케줄 제어</h4>
            <table class="bom-table">
                <tr>
                    <td colspan="2">
                        <div class="td-label">그룹 스케줄 설정</div>
                    </td>
                    <td class="right">
                        <label class="switch-box">
                            <input type="checkbox" v-model="isSchedule" :true-value="1" :false-value="0">
                            <span class="slider round"></span>
                        </label>
                    </td>
                </tr>
                <tr :class="{'disabled-td' : isSchedule === 0}">
                    <td colspan="3">
                        <div class="td-label">Date</div>
                        <ul class="date-ul">
                            <li v-for="item in dateList">
                                <label>
                                    <input type="checkbox" :value="item.id" v-model="date" :disabled="isSchedule === 0"/>
                                    <div>{{item.name}}</div>
                                </label>
                            </li>
                        </ul>
                    </td>
                </tr>
                <tr :class="{'disabled-td' : isSchedule === 0}">
                    <td style="vertical-align: bottom; position:relative;">
                        <div class="td-label">시작시간</div>
                        <label class="input-100">
                            <date-picker v-model="time.start" :config="timeOptions" :disabled="isSchedule === 0"/>
                        </label>
                    </td>
                    <td>~</td>
                    <td style="vertical-align: bottom; position:relative;">
                        <div class="td-label">종료시간</div>
                        <label class="input-100">
                            <date-picker v-model="time.end" :config="timeOptions" :disabled="isSchedule === 0"/>
                        </label>
                    </td>
                </tr>
            </table>

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
                            </div>
                        </draggable>
                    </li>
                </ul>
            </div>

            <div class="modal-footer">
                <button type="button" class="button cancel-button" @click="cancel">취소</button>
                <button type="button" class="button submit-button" @click="submit">등록</button>
            </div>
        </b-modal>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import SimpleVueValidation from 'simple-vue-validator';
    import axios from 'axios';
    import flashModal from '~/components/flashmodal.vue';
    import draggable from 'vuedraggable';

    const {Validator} = SimpleVueValidation;

    export default {

        props: ['propsdata'],
        components: {
            axios,
            flashModal,
            SimpleVueValidation,
            draggable,
        },
        data() {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                id: '',
                name: '',
                barRange: [6, 8],
                isSchedule:1,
                dateList: [],
                date: [],
                time: {
                    start: '00:00',
                    end: '00:00'
                },
                timeOptions: {
                    format: 'HH:mm',
                },
                weekName: ['첫째주', '둘째주', '셋째주', '넷째주', '스케줄 대기 장비'],
                weekList: {
                    week1:
                        [{id: 1, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -1', scheduleWeek: 1},],
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
            };
        },
        validators: {
            name(value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
            this.getDayOfWeek();
        },
        methods: {
            getDayOfWeek (){

                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/dayOfWeek ',
                }).then((res) => {
                    vm.dateList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;

                if (state === 'new') {
                    url = `/api/setting/group`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/setting/group/${vm.id}`;
                    method = 'put';
                    if (!vm.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }

                const params = {
                    id: this.id,
                    name: this.name,
                    costCenter: this.costCenter,
                };

                this.$validate()
                    .then((success) => {
                        if (success) {
                            axios({
                                method: method,
                                url: url,
                                data: params
                            }).then((res) => {
                                if (res.data.code === 1) {
                                    modal.hide('createGroupModal');
                                    vm.msgData.show = true;
                                    vm.msgData.msg = res.data.msg;
                                    vm.$emit('send-message', 1);
                                    vm.$emit('callSearch');
                                }
                            }).catch((error) => {
                                vm.msgData.show = true;
                                vm.msgData.msg = error;
                            });
                        }
                    });
            },
            cancel() {
                this.$bvModal.hide('createGroupModal');
            },
            reset() {
                this.name = '';
                this.name = '';
                this.costCenter ='';
            },
            createdModal() {
                this.state = 'new';
                this.reset();
                this.validation.reset();
            },
            updateModal(e) {
                this.validation.reset();
                this.state = 'update';
                this.id = e.id;
                this.name = e.name;
                this.costCenter = e.costCenter;
            },
        },
    };
</script>
