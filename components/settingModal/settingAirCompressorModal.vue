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
            <div class="modal-body" style="padding:0;">
                <h4 class="modal-h4-title">공기압축기 TP 제어</h4>
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
                <VueSimpleRangeSlider
                        id="bar-range"
                        :min="0"
                        :max="100"
                        v-model="barRange"
                />
                <h4 class="modal-h4-title">전체 공기압축기 스케줄 현황</h4>
                <ul class="all-schedule-list">
                    <li v-for="(week, property, index) in weekList" :class="property" :key="week.id">
                        <div class="td-label">{{weekName[index]}}</div>
                        <div v-if="week.length === 0 && index < 4" class="font-14">
                            스케줄 지정된 장비가 없습니다.
                        </div>
                        <draggable class="list-group" :list="week" group="people" @change="log">
                            <div class="list-group-item equipment-box" v-for="(element, index) in week" :key="element.name">
                                {{ element.name }}
                            </div>
                        </draggable>
                    </li>
                </ul>
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
    import draggable from 'vuedraggable';

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
            draggable
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
                barRange: [30, 80],
                weekName: ['첫째주', '둘째주', '셋째주', '넷째주', '스케줄 대기 장비'],
                weekList: {
                    week1:
                        [{
                            id: 1,
                            equipmentId: 'ingersollrand_rm55',
                            name: 'Ingersoll Rand RM55 -1',
                            scheduleWeek: 1,
                        }],
                    week2: [{
                        id: 1,
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -2',
                        scheduleWeek: 1,
                    }],
                    week3: [{
                        id: 1,
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -3',
                        scheduleWeek: 1,
                    }],
                    week4: [{
                        id: 1,
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -4',
                        scheduleWeek: 1,
                    }],
                    noWeek: [],
                },
            }
        },
        validators: {
            name(value) {
                return Validator.value(value).required();
            }
            ,
        }
        ,
        mounted() {
            this.initSetting();
        }
        ,
        methods: {
            initSetting() {
                this.getAirCompressor();
            }
            ,
            getAirCompressor(id) {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/processes',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.processList = res.data.value;
                        vm.process = vm.processList[0].id;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            }
            ,
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;

                if (state === 'new') {
                    url = `/api/setting/location`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/setting/location/${vm.id}`;
                    method = 'put';
                    if (!vm.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }

                const params = {
                    barRange: this.barRange,
                    date: this.date,
                    time: this.time,
                };

                this.$validate()
                    .then((success) => {
                        if (success) {
                            // form 입력완료
                            axios.post(url, params)
                                .then((res) => {
                                    if (res.data.code === 1) {
                                        // 등록완료시 모달 닫고 초기화 안내메시지 일여주기
                                        modal.hide('createmodal');
                                        vm.msgData.show = true;
                                        vm.msgData.msg = res.data.message;
                                        vm.$emit('send-message', 1);
                                        vm.$emit('callSearch');
                                    }
                                }).catch((error) => {
                                vm.msgData.show = true;
                                vm.msgData.msg = error;
                            });
                        }
                    });
            }
            ,
            cancel() {
                this.$bvModal.hide('createmodal');
            }
            ,
            reset() {
                this.id = '';
                this.barRange = [30, 80];
                this.date = [];
                this.time = {
                    start: '00:00',
                    end: '00:00'
                };
                this.validation.reset();
            }
            ,
            createdModal() {
                this.state = 'new';
                this.reset();
            }
            ,
            updateModal(id) {
                this.validation.reset();
                this.state = 'update';
                this.getAirCompressor(id);
            }
            ,
        }
        ,
    };
</script>
