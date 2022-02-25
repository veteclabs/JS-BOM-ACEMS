<template>
    <div>
        <span :class="`bom-badge`" @click="changePasswordModal()">비밀번호변경</span>
        <passwordModal v-bind:propsdata ="modalData" ref="passwordModal"/>
    </div>
</template>

<script>
    import passwordModal from '~/components/passwordChangeAdminModal.vue'
    export default {
        props: {
            cellData: {
                type: Object,
                default: () => {
                }
            },
        },
        components : {
            passwordModal
        },
        data() {
            return {
                modalData:{
                    show:false,
                },
            };
        },
        methods: {
            //비밀번호 변경 모달 생성
            changePasswordModal: function() {
                this.modalData.show = true;
                this.$refs.passwordModal.createdModal( this.cellData.data.id )
            },
        },
    };
</script>
