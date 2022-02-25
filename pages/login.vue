<template>
    <div id="loginWrap">
        <div id="login">
            <div class="login-img">
                <img src="~assets/images/menu/icn_menu_bom-logo.svg" alt="bomlogo"/>
            </div>
            <div class="login-box">
                <div class="center-box">
                    <div class="login-text-box">
                        <h2 class="font-40">SAMWHA ELECTRIC</h2>
                        <p class="font2-1">
                            Welcome to BOM-Power !
                            <img
                                    src="~assets/images/login/icn_login_waving-hand@2x.png"
                                    alt="icon"
                                    width="12px;"
                            />
                        </p>
                        <p class="font2-1">Login to manage your facility with BOM-Power.</p>
                    </div>
                    <div class="login-form">
                        <label>
                            <input
                                    type="text"
                                    placeholder="ID"
                                    v-model="id"
                                    ref="id"
                                    class="id_input"
                            />
                        </label>
                        <label>
                            <input
                                    type="password"
                                    placeholder="PASSWORD"
                                    v-model="password"
                                    ref="password"
                                    class="password_input"
                                    v-on:keyup.13="login"
                            />
                        </label>
                        <a class="login-btn" @click="login">LOGIN</a>
                    </div>
                </div>
            </div>
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
        company: {
          name: 'VETEC',
        },
        modalMessage: {
          msg: '',
          show: false,
        }, // 로그인 안내모달에 사용할 msg
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
          // eslint-disable-next-line no-alert
          alert('ID를 입력해주세요');
          this.$refs.id.focus();
          return;
        }
        if (this.password === null || this.password === '') {
          // eslint-disable-next-line no-alert
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
          let teamId = this.$store.getters.User.teamId;
          let departmentId = this.$store.getters.User.departmentId;
          if(teamId === 20) {
              this.$router.push('/dashboard');
          }else {
              this.$router.push(`/dashboard/departmentDashboard?depth=${departmentId}`);
          }
      },
    },
  };
</script>
