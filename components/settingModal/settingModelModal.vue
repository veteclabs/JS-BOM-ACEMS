<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="모델관리" hide-footer>
            <table class="bom-table">
                <caption>모델 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>Type</th>
                    <td>
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
                <tr>
                    <th>Maker</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="params.maker" class="input-100" placeholder="메이커를 입력하세요"/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('params.maker') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>Model</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="params.model" class="input-100" placeholder="모델명을 입력하세요"/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('params.model') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>tag</th>
                    <td>
                        <input type="file" @change="onChange" style="margin-bottom:24px;"/>
                    </td>
                </tr>
                <tr v-if="file">
                    <td colspan="2">
                        <xlsx-read :file="file">
                            <xlsx-sheets>
                                <template #default="{sheets}">
                                    <select v-model="selectedSheet">
                                        <option v-for="sheet in sheets" :key="sheet" :value="sheet">
                                            {{ sheet }}
                                        </option>
                                    </select>
                                </template>
                            </xlsx-sheets>
                            <div class="excel-box">
                                <xlsx-table :sheet="selectedSheet"/>
                                <xlsx-json :sheet="selectedSheet" ref="excelJson" style="display:none;">
                                    <template #default="{collection}">
                                        <div>
                                            {{ collection }}
                                        </div>
                                    </template>
                                </xlsx-json>
                            </div>
                        </xlsx-read>
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
    import axios from 'axios';
    import flashModal from '~/components/flashmodal.vue';
    import SimpleVueValidation from 'simple-vue-validator';

    const {Validator} = SimpleVueValidation;
    import {XlsxRead, XlsxTable, XlsxSheets, XlsxJson, XlsxWorkbook, XlsxSheet, XlsxDownload} from "vue-xlsx"

    export default {
        props: ['propsdata'],
        components: {
            axios,
            flashModal,
            XlsxRead, XlsxTable, XlsxSheets, XlsxJson, XlsxWorkbook, XlsxSheet, XlsxDownload
        },
        data: function () {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                typeList: [
                    {id: 1, name: '전력'},
                    {id: 2, name: '온도계'},
                    {id: 3, name: '압력계'},
                    {id: 4, name: '유량계'},
                ],
                componentList: [
                    {id: 1, name: 'a', isActive: true},
                    {id: 2, name: 'b', isActive: true},
                    {id: 3, name: 'c', isActive: true},
                    {id: 4, name: 'd', isActive: true},
                ],
                params: {
                    type: '',
                    maker: '',
                    model: '',
                    tag: '',
                },
                file: null,
                selectedSheet: null,
                sheetName: null,
                sheets: [{name: "SheetOne", data: [{c: 2}]}],
                collection: [{a: 1, b: 2}]
            }
        },
        validators: {
            'params.type': function (value) {
                return Validator.value(value).required();
            },
            'params.maker': function (value) {
                return Validator.value(value).required();
            },
            'params.model': function (value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
            this.getCompressor();
        },
        methods: {
            getCompressor() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/device/compressors',
                }).then((res) => {
                    vm.airCompressorList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                });
            },
            cancel() {
                this.$bvModal.hide('createmodal');
                this.reset();
            },
            reset() {
                this.params = {
                    type: '',
                    maker: '',
                    model: '',
                    tag: '',
                };
                this.validation.reset();
            },
            createdModal() {
                this.state = 'new';
            },
            updateModal(e) {
                this.state = 'update';
                this.params = e;
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;
                this.params.tag = this.$refs.excelJson._data.collection;
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
                                vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                            });
                        }
                    });
            },
            onChange(event) {
                this.file = event.target.files ? event.target.files[0] : null;
            },
        },
    };
</script>
