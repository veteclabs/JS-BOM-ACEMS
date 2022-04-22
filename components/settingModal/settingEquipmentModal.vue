<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="Setting" hide-footer hide-header>
            <div class="modal-header">
                <h5 class="font-20" style="margin:0;">
                    <img src="~assets/images/dashboard/icn_dashboard-modal_setting.png" alt="setting-icon" width="24"/>
                    Setting
                </h5>
                <button aria-label="Close" class="close" @click="cancel">
                    <img src="~assets/images/dashboard/icn_dashboard-modal_close.svg" alt="close" width="24"/>
                </button>
            </div>
            <div class="modal-overflow">
                <h4 class="modal-h4-title first">공기압축기 정보</h4>
                <table class="bom-table">
                    <tr>
                        <td>
                            <div class="td-label">그룹</div>
                            <label class="input-100">
                                <select v-model="params.groupId">
                                    <option :value="null" selected >미지정</option>
                                    <option v-for="group in groupList" :value="group.id" :key="group.id">
                                        {{group.name}}
                                    </option>
                                </select>
                            </label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="td-label">공기압축기명</div>
                            <label class="input-100">
                                <input type="text" v-model="params.name" class="input-100" placeholder="공기압축기명을 입력하세요"/>
                            </label>
                            <div class="err-message" v-if="validation !== undefined">
                                {{ validation.firstError('params.name') }}
                            </div>
                        </td>
                    </tr>
                </table>
                <h4 class="modal-h4-title">공기압축기 압력 제어 범위</h4>
                <table class="bom-table">
                    <tr>
                        <td>
                            <div class="td-label">Min</div>
                            <label class="input-100">
                                <input type="number" v-model="schedule.min" class="input-100" placeholder="최소압력"/>
                            </label>
                        </td>
                        <td>~</td>
                        <td>
                            <div class="td-label">Max</div>
                            <label class="input-100">
                                <input type="number" v-model="schedule.max" class="input-100" placeholder="최대압력"/>
                            </label>
                        </td>
                    </tr>
                </table>
                <div class="err-message" v-if="validation !== undefined">
                    {{ validation.firstError('schedule.min') }}
                </div>
                <!--<VueSimpleRangeSlider id="bar-range" :min="0" :max="12" v-model="barRange"/>-->
                <h4 class="modal-h4-title">공기압축기 스케줄</h4>
                <div class="err-message" v-if="validation !== undefined">
                    {{ validation.firstError('schedule.isActive') }}
                </div>
                <table class="bom-table">
                    <tr>
                        <td colspan="2">
                            <div class="td-label">개별 스케줄 설정</div>
                        </td>
                        <td class="right">
                            <label class="switch-box">
                                <input type="checkbox" v-model="schedule.isActive" @change="groupCheck">
                                <span class="slider round"></span>
                            </label>
                        </td>
                    </tr>
                    <tr :class="{'disabled-td' : !schedule.isActive}">
                        <td colspan="3">
                            <div class="td-label">Date</div>
                            <ul class="date-ul">
                                <li v-for="item in dateList">
                                    <label>
                                        <input type="checkbox" :value="item" v-model="schedule.dayOfWeeks"
                                               :disabled="!schedule.isActive"/>
                                        <div>{{item.name}}</div>
                                    </label>
                                </li>
                            </ul>
                        </td>
                    </tr>
                    <tr :class="{'disabled-td' : !schedule.isActive}">
                        <td style="vertical-align: bottom; position:relative;">
                            <div class="td-label">시작시간</div>
                            <label class="input-100">
                                <date-picker v-model="schedule.startTime" :config="timeOptions"
                                             :disabled="!schedule.isActive"/>
                            </label>
                        </td>
                        <td>~</td>
                        <td style="vertical-align: bottom; position:relative;">
                            <div class="td-label">종료시간</div>
                            <label class="input-100">
                                <date-picker v-model="schedule.stopTime" :config="timeOptions"
                                             :disabled="!schedule.isActive"/>
                            </label>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="button cancel-button" @click="cancel">취소</button>
                <button type="button" class="button submit-button" @click="submit">설정</button>
            </div>
        </b-modal>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import SimpleVueValidation from 'simple-vue-validator';
    import axios from 'axios';
    import flashModal from '~/components/flashmodal.vue';
    import VueSimpleRangeSlider from 'vue-simple-range-slider';
    import 'vue-simple-range-slider/dist/vueSimpleRangeSlider.css';

    const {Validator} = SimpleVueValidation;

    SimpleVueValidation.extendTemplates({
        required: '필수 입력 항목입니다.',
        length: '길이가 {0} 이어야 합니다.',
        minLength: '{0} 글자 이상이어야 합니다.',
        maxLength: '{0} 글자 이하여야 합니다.',
        digit: '숫자만 입력해주세요.',
    });

    export default {

        props: ['propsdata'],
        components: {
            axios,
            flashModal,
            SimpleVueValidation,
            VueSimpleRangeSlider,
        },
        data() {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                params: {},
                schedule: {
                    dayOfWeeks: [],
                    id: '',
                    isActive: false,
                    min: 0,
                    max: 0,
                    startTime: '00:00:00',
                    stopTime: '00:00:00',
                },
                id: '',
                name: '',
                dateList: [],
                timeOptions: {
                    format: 'HH:mm:00',
                },
                groupList: [],

            };
        },
        validators: {
            'params.name': function (value) {
                return Validator.value(value).required();
            },
            'schedule.min, schedule.max': function (min, max) {
                return Validator.value(min).required().custom(function () {
                    if ((min !== null && max !== null)) {
                        if (max < min) {
                            return 'Max 값을 더 높게 설정해주세요.'
                        } else if (min === max) {
                            return 'Min, Max 값을 다르게 설정해주세요.'
                        }
                    }
                });
            },
            'schedule.isActive, schedule.dayOfWeeks, schedule.startTime, schedule.stopTime':
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
            this.getGroup();
            this.getDayOfWeek();
        },
        methods: {
            getDayOfWeek () {
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
            getGroup() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/device/groups',
                }).then((res) => {
                    vm.groupList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            groupCheck() {
                const vm = this;
                console.log(vm.params.groupId)
                console.log(vm.schedule.isActive)
                if(vm.params.groupId !== null) {
                    if(vm.schedule.isActive) {
                        alert("그룹이 선택된 장비는 개별제어 설정이 불가합니다.");
                        vm.schedule.isActive = false;
                    }
                }
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;


                if (state === 'new') {
                    url = `/api/compressor`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/compressor/${vm.params.id}`;
                    method = 'put';
                    if (!vm.params.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }
                vm.params.schedule = vm.schedule;
                vm.params.schedule.min = Number( vm.params.schedule.min);
                vm.params.schedule.max = Number( vm.params.schedule.max);
                this.$validate()
                    .then((success) => {
                        if (success) {
                            // form 입력완료
                            axios({
                                method: method,
                                url: url,
                                data: vm.params
                            }).then((res) => {
                                // 등록완료시 모달 닫고 초기화 안내메시지 일여주기
                                modal.hide('createmodal');
                                vm.msgData.show = true;
                                vm.msgData.msg = res.data.message;
                                vm.$emit('send-message', 1);
                                vm.$emit('callSearch');
                            }).catch((error) => {
                                vm.msgData.show = true;
                                vm.msgData.msg = error;
                            });
                        }
                    });
            },
            cancel() {
                this.$bvModal.hide('createmodal');
                this.reset();
            },
            reset() {
                this.params = {
                    name: '',
                };
                this.schedule = {
                    dayOfWeeks: [],
                    isActive: false,
                    min: 0,
                    max: 0,
                    startTime: '00:00:00',
                    stopTime: '00:00:00',
                };
                this.validation.reset();
            },
            createdModal() {
                this.state = 'new';
                this.reset();
            },
            updateModal(e) {
                console.log(e)
                this.validation.reset();
                this.state = 'update';
                this.params = JSON.parse(JSON.stringify(e));
                if (e.schedule !== null) {
                    this.schedule = this.params.schedule;
                }
            },
        },
    };
</script>
