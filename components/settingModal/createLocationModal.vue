<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="시설 관리" hide-footer>
            <table class="bom-table">
                <caption>시설 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>부서</th>
                    <td>
                        <label class="input-100">
                            <select v-model="department" class="input-100">
                                <option v-for="list in departmentList" v-bind:value="list.id" :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('department') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>변전실</th>
                    <td>
                        <label class="input-100">
                            <select v-model="subStation" class="input-100">
                                <option v-for="list in subStationList" v-bind:value="list.id" :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('subStation') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>판넨</th>
                    <td>
                        <label class="input-100">
                            <select v-model="panelBoard" class="input-100">
                                <option v-for="list in panelBoardList" v-bind:value="list.id" :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('panelBoard') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>스위치</th>
                    <td>
                        <label class="input-100">
                            <select v-model="switchBoard" class="input-100">
                                <option v-for="list in switchBoardList" v-bind:value="list.id" :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('switchBoard') }}
                        </div>
                    </td>
                </tr>

                <tr>
                    <th>공정</th>
                    <td>
                        <label class="input-100">
                            <select v-model="process" class="input-100">
                                <option v-for="list in processList" v-bind:value="list.id" :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('process') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>시설명</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="description" class="input-100"
                                   placeholder="시설명을 입력해주세요 "/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('description') }}
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
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
                id: '',
                description: '',
                subStation: '',
                subStationList: [],
                panelBoard: '',
                panelBoardList: [],
                switchBoard: '',
                switchBoardList: [],
                department: '',
                departmentList: [],
                process: '',
                processList: [],

            };
        },
        validators: {
            description(value) {
                return Validator.value(value).required();
            },
            subStation(value) {
                return Validator.value(value).required();
            },
            panelBoard(value) {
                return Validator.value(value).required();
            },
            switchBoard(value) {
                return Validator.value(value).required();
            },
            department(value) {
                return Validator.value(value).required();
            },
            process(value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
            this.initSetting();
        },
        methods: {
            initSetting() {
                this.getSubStation();
                this.getPanelBoard();
                this.getSwitchBoard();
                this.getDepartment();
                this.getProcess();
            },
            getSubStation() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/substations',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.subStationList = res.data.value;
                        vm.subStation = vm.subStationList[0].id;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            getPanelBoard() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/panelBoards',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.panelBoardList = res.data.value;
                        vm.panelBoard = vm.panelBoardList[0].id;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            getSwitchBoard() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/switchBoards',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.switchBoardList = res.data.value;
                        vm.switchBoard = vm.switchBoardList[0].id;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            getDepartment() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/departments',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.departmentList = res.data.result;
                        vm.department = vm.departmentList[0].id;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            getProcess() {
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
            },
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
                    description: this.description,
                    substationId: this.subStation,
                    panelBoardId: this.panelBoard,
                    switchBoardId: this.switchBoard,
                    departmentId: this.department,
                    manufacturingProcessId: this.process
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
                                        vm.getLocationList();
                                    }
                                }).catch((error) => {
                                vm.msgData.show = true;
                                vm.msgData.msg = error;
                            });
                        }
                    });
            },
            cancel() {
                this.$bvModal.hide('createmodal');
            },
            reset() {
                this.id = '';
                this.description = '';
                this.subStation = this.subStationList[0].id;
                this.panelBoard = this.panelBoardList[0].id;
                this.switchBoard = this.switchBoardList[0].id;
                this.department = this.departmentList[0].id;
                this.process = this.processList[0].id;
                this.validation.reset();
            },
            createdModal() {
                this.state = 'new';
                this.reset();
            },
            updateModal(e) {
                this.validation.reset();
                this.state = 'update';
                this.id = e.id;
                this.description = e.description;
                this.subStation = e.substationId;
                this.panelBoard = e.panelBoardId;
                this.switchBoard = e.switchBoardId;
                this.department = e.departmentId;
                this.process = e.manufacturingProcessId;
            },
        },
    };
</script>
