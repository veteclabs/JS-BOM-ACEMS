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
                                <select v-model="params.groupId" @change="groupCheck">
                                    <option :value="null" selected>미지정</option>
                                    <option v-for="group in groupList" :value="group.id" :key="group.id"
                                            @change="getDayOfWeek">
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
                    <tr>
                        <td>
                            <div class="td-label">공기압축기 모델</div>
                            <label>
                                <select v-model="params.equipment.maker" @change="getModel(true)">
                                    <option v-for="maker in makerList" :value="maker" :key="maker">
                                        {{maker}}
                                    </option>
                                </select>
                            </label>
                            <label>
                                <select v-model="params.equipment.equipmentId">
                                    <option v-for="model in modelList" :value="model.equipmentId"
                                            :key="model.equipmentId">
                                        {{model.model}}
                                    </option>
                                </select>
                            </label>
                            <div class="err-message" v-if="validation !== undefined">
                                {{ validation.firstError('params.equipment.maker') }}
                            </div>
                            <div class="err-message" v-if="validation !== undefined">
                                {{ validation.firstError('params.equipment.equipmentId') }}
                            </div>
                        </td>
                    </tr>
                </table>
                <h4 v-if="params.state.COMP_StartPre" class="modal-h4-title">공기압축기 압력 제어 범위</h4>
                <table class="bom-table">
                    <tr v-if="params.state.COMP_StartPre">
                        <td>
                            <div class="td-label">Min</div>
                            <label class="input-100">
                                <input type="number" v-model="params.schedule.min" class="input-100"
                                       placeholder="최소압력"/>
                            </label>
                        </td>
                        <td>~</td>
                        <td>
                            <div class="td-label">Max</div>
                            <label class="input-100">
                                <input type="number" v-model="params.schedule.max" class="input-100" placeholder="최대압력"
                                       @change="settingMin"/>
                            </label>
                        </td>
                    </tr>

                </table>
                <div class="err-message" v-if="validation !== undefined">
                    {{ validation.firstError('params.schedule.min') }}
                </div>
                <h4 class="modal-h4-title">공기압축기 스케줄</h4>
                <div class="err-message" v-if="validation !== undefined">
                    {{ validation.firstError('params.schedule.isActive') }}
                </div>
                <table class="bom-table">
                    <tr>
                        <td colspan="2">
                            <div class="td-label">개별 스케줄 설정</div>
                        </td>
                        <td class="right">
                            <label class="switch-box">
                                <input type="checkbox" v-model="params.schedule.isActive"
                                       @change="groupCheck"
                                       ref="scheduleCheck"/>
                                <span class="slider round"></span>
                            </label>
                        </td>
                    </tr>
                    <tr :class="{'disabled-td' : !params.schedule.isActive}">
                        <td colspan="3">
                            <div class="td-label">Date</div>
                            <ul class="date-ul">
                                <li v-for="item in dateList" :key="item.name">
                                    <label :class="{'disabled' :!item.selectable}">
                                        <input type="checkbox" :value="item" v-model="params.schedule.dayOfWeeks"
                                               :disabled="!params.schedule.isActive || !item.selectable"/>
                                        <div>{{item.name}}</div>
                                    </label>
                                </li>
                            </ul>
                        </td>
                    </tr>
                    <tr :class="{'disabled-td' : !params.schedule.isActive}">
                        <td style="vertical-align: bottom; position:relative;">
                            <div class="td-label">시작시간</div>
                            <label class="input-100">
                                <date-picker v-model="params.schedule.startTime" :config="timeOptions"
                                             :disabled="!params.schedule.isActive"/>
                            </label>
                        </td>
                        <td>~</td>
                        <td style="vertical-align: bottom; position:relative;">
                            <div class="td-label">종료시간</div>
                            <label class="input-100">
                                <date-picker v-model="params.schedule.stopTime" :config="timeOptions"
                                             :disabled="!params.schedule.isActive"/>
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
    import qs from 'qs';


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
        },
        data() {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                params: {
                    equipment: {
                        equipmentId: '',
                        model: '',
                    },
                    state: {
                        COMP_StartPre: '',
                    },
                    schedule: {
                        dayOfWeeks: [],
                        id: '',
                        isActive: false,
                        min: 0,
                        max: 0,
                        startTime: '00:00:00',
                        stopTime: '00:00:00',
                    },
                },
                id: '',
                groupScheduleActive: false,
                dateList: [],
                makerList: [],
                modelList: [],
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
            'params.equipment.maker': function (value) {
                return Validator.value(value).required();
            },
            'params.equipment.equipmentId': function (value) {
                return Validator.value(value).required();
            },
            'params.schedule.isActive, params.schedule.dayOfWeeks, params.schedule.startTime, params.schedule.stopTime':
                function (active, day, start, stop) {
                    if (active !== undefined && day !== undefined && start !== undefined && stop !== undefined) {
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
                    }
                },
        },
        mounted() {
            this.getMaker();
            this.getGroup();
        },
        methods: {
            getMaker() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/compressor/maker'
                }).then((res) => {
                    if (res.status === 200) {
                        vm.makerList = res.data;
                        vm.getModel();
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            getModel(self) {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/compressor/model',
                    params: {
                        maker: vm.params.equipment.maker
                    }
                }).then((res) => {
                    if (res.status === 200) {
                        vm.modelList = res.data;
                        if (self && vm.modelList.length !== 0) {
                            vm.params.equipment.equipmentId = vm.modelList[0].equipmentId;
                        }
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            settingMin() {
                const vm = this;
                const max = vm.params.schedule.max;
                const min = vm.params.schedule.min;
            },
            getDayOfWeek() {
                const vm = this;
                let url = '';
                if (this.params.groupId) {
                    url = `/api/dayOfWeek/${this.params.groupId}`
                } else {
                    url = `/api/dayOfWeek`
                }
                axios({
                    method: 'get',
                    url: url
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
                    if (res.status === 200) {
                        vm.groupList = res.data;
                        this.getDayOfWeek();
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            groupCheck() {
                const vm = this;
                if (vm.params.groupId !== null) {
                    return vm.getGroupInfo(vm.params.groupId);
                }
            },
            async submit() {
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
                vm.params.schedule.min = Number(vm.params.schedule.min);
                vm.params.schedule.max = Number(vm.params.schedule.max);

                this.$validate()
                    .then((success) => {
                        if (success) {
                            axios({
                                method: method,
                                url: url,
                                data: vm.params
                            }).then((res) => {
                                modal.hide('createmodal');
                                vm.msgData.show = true;
                                vm.msgData.msg = res.data.message;
                                vm.$emit('send-message', 1);
                                vm.$emit('callSearch');
                            }).catch((error) => {
                                vm.msgData.show = true;
                                vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
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
                    groupId: null,
                    name: '',
                    equipment: {
                        equipmentId: '',
                        maker: '',
                    },
                    state: {
                        COMP_StartPre: '',
                    },
                    schedule: {
                        dayOfWeeks: [],
                        isActive: false,
                        min: 0,
                        max: 0,
                        startTime: '00:00:00',
                        stopTime: '00:00:00',
                    }
                };
                this.groupScheduleActive = false;
                this.validation.reset();
            },
            createdModal() {
                this.state = 'new';
                this.reset();
            },
            async updateModal(id) {
                const vm = this;
                vm.validation.reset();
                axios.get(`/api/compressor/${id}`, {
                    params: {
                        components: ["stateComponent"]

                    }, paramsSerializer: params => {
                        return qs.stringify(params)
                    }
                }).then((res) => {
                    if (res.status === 200) {
                        vm.state = 'update';
                        vm.params = res.data;
                        vm.getDayOfWeek();
                        vm.getModel();
                    } else {
                        vm.msgData.msg = res.error;
                    }
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                });
            },
            async getGroupInfo(id) {
                const vm = this;
                axios({
                    method: 'get',
                    url: `/api/group/${id}`,
                }).then((res) => {
                    if (res.status === 200) {
                        vm.groupScheduleActive = res.data.schedule.isActive;
                        if (vm.groupScheduleActive) {
                            alert("그룹제어 중인 장비는 개별제어 설정이 불가합니다.");
                            vm.params.schedule.isActive = false;
                            vm.$refs.scheduleCheck.checked = false;
                            return false;
                        } else {
                            return true;
                        }
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                });
            },
        },
    };
</script>
