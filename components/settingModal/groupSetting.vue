<template>
    <div>
        <div class="ibox">
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
                            <input type="text" v-model="groupData.name" class="input-100"
                                   placeholder="그룹명을 입력해주세요 "/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('groupData.name') }}
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="ibox">
            <div class="ibox-title ibox-noborder-title flex-box">
                공기압축기 압력 제어 범위
            </div>
            <table class="bom-table">
                <tr>
                    <td>
                        <div class="td-label">Min</div>
                        <label class="input-100">
                            <input type="number" v-model="groupData.schedule.min" class="input-100" placeholder="최소압력"/>
                        </label>
                    </td>
                    <td>~</td>
                    <td>
                        <div class="td-label">Max</div>
                        <label class="input-100">
                            <input type="number" v-model="groupData.schedule.max" class="input-100" placeholder="최대압력"
                            @change="settingMin"/>
                        </label>
                    </td>
                </tr>
            </table>
            <div class="err-message" v-if="validation !== undefined">
                {{ validation.firstError('groupData.schedule.min') }}
            </div>
        </div>
        <div class="ibox">
            <div class="ibox-title ibox-noborder-title flex-box">
                <div>그룹 스케줄 제어</div>
                <label class="switch-box">
                    <input type="checkbox" v-model="groupData.schedule.isActive">
                    <span class="slider round"></span>
                </label>
            </div>
            <div class="err-message" v-if="validation !== undefined">
                {{ validation.firstError('groupData.schedule.isActive') }}
            </div>
            <table class="bom-table">
                <tr :class="{'disabled-td' : !groupData.schedule.isActive}">
                    <td colspan="3">
                        <div class="td-label">Date</div>
                        <ul class="date-ul">
                            <li v-for="item in dateList">
                                <label>
                                    <input type="checkbox" :value="item" v-model="groupData.schedule.dayOfWeeks"
                                           :disabled="!groupData.schedule.isActive"/>
                                    <div>{{item.name}}</div>
                                </label>
                            </li>
                        </ul>
                    </td>
                </tr>
                <tr :class="{'disabled-td' : !groupData.schedule.isActive}">
                    <td style="vertical-align: bottom; position:relative;">
                        <div class="td-label">시작시간</div>
                        <label class="input-100">
                            <date-picker v-model="groupData.schedule.startTime" :config="timeOptions"
                                         :disabled="!groupData.schedule.isActive"/>
                        </label>
                    </td>
                    <td>~</td>
                    <td style="vertical-align: bottom; position:relative;">
                        <div class="td-label">종료시간</div>
                        <label class="input-100">
                            <date-picker v-model="groupData.schedule.stopTime" :config="timeOptions"
                                         :disabled="!groupData.schedule.isActive"/>
                        </label>
                    </td>
                </tr>
            </table>
        </div>
        <div class="ibox" v-if="groupData.schedule.weekDevices">
            <div class="ibox-title ibox-noborder-title">공기압축기 전체 스케줄 제어</div>
            <div class="ibox-content">
                <div :class="{'disabled-ibox' : !groupData.schedule.isActive, 'all-schedule-list':true}">
                    <ul v-for="(targetWeek) in groupData.schedule.weekDevices">
                        <div class="td-label">{{targetWeek.name}}</div>
                        <li v-if="targetWeek.working.length === 0">스케줄 장비가 없습니다.</li>
                        <draggable :group="`week${targetWeek.id}`" :list="targetWeek.working">
                            <li v-for="equipment in targetWeek.working" :key="equipment.id">
                                <div class="equipment-box">{{ equipment.name }}</div>
                            </li>
                        </draggable>
                        <div class="td-label" style="margin:48px 0 0 0">스케줄 대기장비</div>
                        <li v-if="targetWeek.stanBy && targetWeek.standBy.length === 0">대기 장비가 없습니다.</li>
                        <draggable :group="`week${targetWeek.id}`" :list="targetWeek.standBy">
                            <li v-for="equipment in targetWeek.standBy" class="no-schedule-li"
                                :key="equipment.id">
                                <div class="equipment-box">{{ equipment.name }}</div>
                            </li>
                        </draggable>
                    </ul>

                </div>
            </div>
        </div>
        <b-modal v-model="successModal.show" title="알림" ok-only auto-focus-button="ok"
                 @ok="handleOk">
            <div>
                {{ successModal.msg }}
            </div>
        </b-modal>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import axios from 'axios';
    import SimpleVueValidation from 'simple-vue-validator';
    import draggable from 'vuedraggable';
    import flashModal from '~/components/flashmodal.vue';

    const {Validator} = SimpleVueValidation;

    export default {

        props: ['groupData'],
        components: {
            axios,
            SimpleVueValidation,
            draggable,
            flashModal,
        },
        data() {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                successModal: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                dateList: [],
                timeOptions: {
                    format: 'HH:mm:00',
                },
            };
        },
        validators: {
            'groupData.name'(value) {
                return Validator.value(value).required();
            },
            'groupData.schedule.isActive, groupData.schedule.dayOfWeeks, groupData.schedule.startTime, groupData.schedule.stopTime':
                function (active, day, start, stop) {
                    return Validator.value(active).required().custom(function () {
                        if (active) {
                            if (day.length === 0) {
                                return '요일을 선택해주세요'
                            }
                            if (start === null || stop === null || start === undefined || stop === undefined) {
                                return '시간을 선택해주세요'
                            }
                        }
                    });
                },
        },
        mounted() {
            this.getDayOfWeek();
        },
        methods: {
            settingMin() {
                const vm = this;
                const max = vm.groupData.schedule.max;
                const min = vm.groupData.schedule.min;
            },
            getDayOfWeek() {
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
            validationCheck() {
                const vm = this;
                const params = this.groupData;
                axios({
                    method: 'PUT',
                    url: `/api/groups/check/schedule/${params.id}`,
                    headers: {
                        setting: true,
                    },
                    data: params
                }).then((res) => {
                    if(res.data.length === 0) {
                         vm.submit("update");
                    }else {
                        let duplicationDate = '';
                        res.data.forEach(item => {
                            duplicationDate = `${duplicationDate} ${item.name} (${item.dayOfWeekName}),`
                        });
                        if(confirm(`
그룹 스케줄 제어 요일 중 개별 공기압축기에서 제어 중인 요일이 포함되어있습니다. 저장 시 개별 공기압축기 제어에서 해당 요일은 제외 됩니다.

중복 공기압축기 :
${duplicationDate}

계속 진행하시겠습니가?
                        `)) {
                            vm.submit("update");

                        }else {
                            alert("취소 되었습니다.")
                        }
                    }

                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            submit(state) {
                const vm = this;
                const params = this.groupData;


                let url, method;
                if (state === 'new') {
                    url = `/api/group`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/group/${params.id}`;
                    method = 'put';
                    if (!params.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }

                params.schedule.min = Number(params.schedule.min);
                params.schedule.max = Number(params.schedule.max);

                if(params.schedule.isActive) {
                    let deviceArray = params.schedule.weekDevices;
                    let targetWeek ='';
                    deviceArray.forEach(target => {
                        if(target.working.length === 0) {
                            targetWeek = `${targetWeek} ${target.name},`;
                        }
                    });
                    if(targetWeek !== '') {
                        targetWeek = targetWeek.slice(0, -1);
                        if(confirm(`공기압축기가 지정되지 않은 스케줄 주차가 있습니다.
이대로 실행할 경우 해당 주차에는 공기압축기가 동작하지 않습니다.

스케줄 장비 미지정 주차 : ${targetWeek}`)) {}
                        else {
                            return false;
                        }
                    }
                }

                this.$validate().then((success) => {
                    if (success) {
                        axios({
                            method: method,
                            url: url,
                            data: params
                        }).then((res) => {
                            vm.successModal.show = true;
                            vm.successModal.msg = res.data.message;
                        }).catch((error) => {
                            vm.msgData.show = true;
                            vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                        });
                    }
                });
            },
            handleOk: function () {
                if (this.$route.name.indexOf('setting') !== -1) {
                    this.$router.push('/setting/group');
                } else {
                    this.$emit('modalClose', 1);
                }
            },
        },
    };
</script>
