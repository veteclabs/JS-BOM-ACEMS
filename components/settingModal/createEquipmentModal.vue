<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="장비 관리" hide-footer>
            <table class="bom-table">
                <caption>장비 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>Type</th>
                    <td colspan="2">
                        <label class="input-100">
                            <select v-model="params.type" class="input-100">
                                <option v-for="list in typeList" v-bind:value="list.name" :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('params.type') }}
                        </div>
                    </td>
                </tr>
                <tr v-if="params.type === '전력'">
                    <th>Model</th>
                    <td colspan="2">
                        <label class="input-100">
                            <select v-model="params.model" class="input-100">
                                <option v-for="list in modelList" v-bind:value="list.name" :key="list.id"
                                        v-if="list.type === params.type">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                    </td>
                </tr>
                <tr v-if="params.type === '전력' && params.model === '남전사'">
                    <th>전압</th>
                    <td colspan="2">
                        <label class="radio-box" v-for="list in voltageList" :key="list.id">
                            <input type="radio" v-model="params.voltage" :value="list.id"/>
                            <div class="radio-circle">
                                <div class="inner-circle"/>
                            </div>
                            <div class="radio-label">{{ list.name }}</div>
                        </label>
                    </td>
                </tr>
                <tr v-if="params.type === '전력' && params.model === '남전사'">
                    <th>비율</th>
                    <td>
                        <div class="td-label">CT비</div>
                        <label class="input-100">
                            <input type="number" v-model="params.ct" class="input-100" placeholder="CT비"/>
                        </label>
                    </td>
                    <td>
                        <div class="td-label">PT비</div>
                        <label class="input-100">
                            <input type="number" v-model="params.pt" class="input-100" placeholder="PT비"/>
                        </label>
                    </td>
                </tr>
                <tr>
                    <th>
                        <div v-if="params.type === '전력'">공기압축기</div>
                        <div v-else>그룹</div>
                    </th>
                    <td>
                        <label class="input-100" v-if="params.type === '전력'">
                            <select v-model="params.groupId">
                                <option v-for="group in airCompressorList" :value="group.id" :key="group.id">
                                    {{group.name}}
                                </option>
                            </select>
                        </label>
                        <label class="input-100" v-else>
                            <select v-model="params.groupId">
                                <option :value="null" selected>미지정</option>
                                <option v-for="group in groupList" :value="group.id" :key="group.id">
                                    {{group.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('params.groupId') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>장비명</th>
                    <td colspan="2">
                        <label class="input-100">
                            <input type="text" v-model="params.name" class="input-100"
                                   placeholder="장비명을 입력해주세요 "/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('params.name') }}
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

    export default {

        props: ['propsdata'],
        components: {
            axios,
            flashModal,
            SimpleVueValidation,
        },
        data: function () {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                params: {},
                typeList: [
                    {id: 1, name: '전력'},
                    {id: 2, name: '온도계'},
                    {id: 3, name: '압력계'},
                    {id: 4, name: '유량계'},
                ],
                modelList: [
                    {id: 1, type: '전력', name: 'Accura'},
                    {id: 2, type: '전력', name: '남전사'},
                ],
                voltageList: [
                    {id: 'HIGH', name: '고압'},
                    {id: 'LOW', name: '저압'}
                ],
                groupList: [],
                airCompressorList: [],
            }
        },
        validators: {
            'params.type': function (value) {
                return Validator.value(value).required();
            },
            'params.groupId': function (value) {
                const vm = this;
                return Validator.value(value).custom(function () {
                    if (vm.params.type === '전력') {
                        if (value === null || value === '' || value === undefined) {
                            return '필수 입력 항목입니다.'
                        }
                    }
                })
            },
            'params.name': function (value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
            this.getGroup();
            this.getCompressor();
        },
        methods: {
            getGroup() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/device/groups',
                }).then((res) => {
                    vm.groupList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message;
                });
            },
            getCompressor() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/device/compressors',
                }).then((res) => {
                    vm.airCompressorList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message;
                });
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;


                if (state === 'new') {
                    url = `/api/etcs`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/etc/${vm.params.id}`;
                    method = 'put';
                    if (!vm.params.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }

                this.$validate()
                    .then((success) => {
                        if (success) {
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
                    groupId: null,
                    name: null,
                    model: null,
                    type: null,
                    maker: null,
                    ct: null,
                    pt: null,
                    voltage: null
                };
                this.validation.reset();
            },
            createdModal() {
                this.state = 'new';
                this.reset();
            },
            updateModal(e) {
                this.validation.reset();
                this.state = 'update';
                this.params = JSON.parse(JSON.stringify(e));
            },
        },
    };
</script>
