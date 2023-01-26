<template>
    <div id="loginWrap">
        <div class="login-bg">
        </div>
        <div id="login">
            <div class="login-box">
                <div class="center-box">
                    <div class="login-text-box">
                        <h2 class="font-48">BOM-ACEMS</h2>
                        <p class="font2-1">
                            Welcome to BOM-ACEMS!
                            <img src="~assets/images/login/icn_login_waving-hand.png" alt="icon" width="12px;"/>
                        </p>
                        <p class="font2-1">Login to manage your facility with BOM-ACEMS.</p>
                    </div>
                    <div class="login-form">
                        <label>
                            <input type="text" placeholder="ID" v-model="id" ref="id" class="id_input"/>
                        </label>
                        <label>
                            <input type="password" placeholder="PASSWORD" v-model="password" ref="password"
                                   class="password_input"
                                   v-on:keyup.13="login"
                            />
                        </label>
                        <a class="login-btn" @click="login">LOGIN</a>
                    </div>
                </div>
            </div>
        </div>
        <div class="copyright-box">
            Copyrightⓒ 정석이엔지 Co., Ltd. All rights reserved.
        </div>
        <flashModal v-bind:propsdata="modalMessage"/>
    </div>
</template>
<script>
    import flashModal from '~/components/flashmodal.vue';

    export default {

        layout: 'login',
        components: {
            flashModal,
        },
        data() {
            return {
                id: '',
                password: '',
                modalMessage: {
                    msg: '',
                    show: false,
                },
            };
        },
        mounted() {
            document.body.className = 'login-body';
        },
        destroyed() {
            document.body.className = '';
        },
        methods: {
            async login() {
                if (this.id === null || this.id === '') {
                    alert('ID를 입력해주세요');
                    this.$refs.id.focus();
                    return;
                }
                if (this.password === null || this.password === '') {
                    alert('패스워드를 입력해주세요');
                    this.$refs.password.focus();
                    return;
                }
                try {
                    await this.$store
                        .dispatch('login', {
                            id: this.id,
                            password: this.password,
                        })
                        .then(() => this.redirect());
                } catch (e) {
                    this.modalMessage.msg = e.message;
                    this.modalMessage.show = true;
                }
            },
            redirect() {
                this.$router.push('/dashboard');
            },
        },
    };
</script>
