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
                            <select v-model="type" class="input-100">
                                <option v-for="list in typeList" v-bind:value="list.id" :key="list.id"
                                @change = "">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('type') }}
                        </div>
                    </td>
                </tr>
                <tr v-if="type === 1">
                    <th>Model</th>
                    <td colspan="2">
                        <label class="input-100">
                            <select v-model="model" class="input-100">
                                <option v-for="list in modelList" v-bind:value="list.id" :key="list.id"
                                v-if="list.type === type">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('model') }}
                        </div>
                    </td>
                </tr>
                <tr v-if="type===1 && model === 1">
                    <th>전압</th>
                    <td colspan="2">
                        <label class="radio-box" v-for="list in voltageList" :key="list.id">
                            <input type="radio" v-model="voltage" :value="list.id"/>
                            <div class="radio-circle">
                                <div class="inner-circle"/>
                            </div>
                            <div class="radio-label">{{ list.name }}</div>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('voltage') }}
                        </div>
                    </td>
                </tr>
                <tr v-if="type===1 && model === 1">
                    <th>비율</th>
                    <td>
                        <div class="td-label">CT비</div>
                        <label class="input-100">
                            <input type="number" v-model="CT" class="input-100" placeholder="CT비"/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('CT') }}
                        </div>
                    </td>
                    <td>
                        <div class="td-label">PT비</div>
                        <label class="input-100">
                            <input type="number" v-model="PT" class="input-100" placeholder="PT비"/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('PT') }}
                        </div>
                    </td>
                </tr>
                <tr v-if="type !== 1">
                    <th>TANK</th>
                    <td>
                        <label class="input-100">
                            <select v-model="tank">
                                <option v-for="tank in tankList" :key="tank.id">
                                    {{tank.name}}
                                </option>
                            </select>
                        </label>
                    </td>
                </tr>
                <tr v-if="type === 1">
                    <th>공기압축기</th>
                    <td>
                        <label class="input-100">
                            <select v-model="airCompressor">
                                <option v-for="comp in airCompressorList" :key="comp.id">
                                    {{comp.name}}
                                </option>
                            </select>
                        </label>
                    </td>
                </tr>
                <tr>
                    <th>장비명</th>
                    <td colspan="2">
                        <label class="input-100">
                            <input type="text" v-model="description" class="input-100"
                                   placeholder="장비명을 입력해주세요 "/>
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
        data: function () {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                id: '',
                description: '',
                type: 1,
                typeList: [
                    {id: 1, name: '전력'},
                    {id: 2, name: '온도계'},
                    {id: 3, name: '압력계'},
                    {id: 4, name: '유량계'},
                ],
                model: '',
                modelList: [
                    {id: 1, type: 1, name: 'Accura'},
                    {id: 2, type: 1, name: '남전사'},/*
                    {id: 3, type: 2, name: 'Ingersoll Rand 100'},
                    {id: 4, type: 2, name: 'Ingersoll Rand 200'},
                    {id: 5, type: 2, name: 'Ingersoll Rand 150'},
                    {id: 6, type: 2, name: 'YUJIN 100'},*/
                ],
                voltage: 1,
                voltageList: [
                    {id: 1, name: '고압'},
                    {id: 2, name: '저압'}
                ],
                CT:0,
                PT:0,
                tank:1,
                tankList: [
                    {id: 1, name: 'tank1'},
                    {id: 2, name: 'tank2'},
                ],
                airCompressor:1,
                airCompressorList: [
                    {
                        id: 1,
                        unit: 'U001',
                        state: 'RUN',
                        alarm: '',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -1'
                    },
                    {
                        id: 2,
                        unit: 'U002',
                        state: 'STOP',
                        alarm: '',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -2'
                    },
                    {
                        id: 3,
                        unit: 'U003',
                        state: 'LOAD',
                        alarm: '',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -3'
                    },
                    {
                        id: 4,
                        unit: 'U004',
                        state: 'UNLOAD',
                        alarm: '온도 2단계 알람이 발생했습니다.',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -4'
                    },
                ],
            }
        },
        validators: {
            description(value) {
                return Validator.value(value).required();
            },
            model(value) {
                return Validator.value(value).required();
            },
            type(value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
        },
        methods: {
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
                    modelId: this.model,
                    typeId: this.type,
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
                this.model = this.modelList[0].id;
                this.type = this.typeList[0].id;
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
                this.model = e.modelId;
                this.type = e.typeId;
            },
        },
    };
</script>
