<template>
    <div></div>
</template>
<script>
    import axios from "axios";

    export default {
        data() {
            return {
                role: "",
            };
        },
        mounted() {
            this.roleChk();
        },
        methods: {
            roleChk: function () {
                let vm = this;
                vm.role = this.$store.getters.User.role_id;
                if (vm.role > 2) {
                    alert("해당 메뉴는 관리자 전용 메뉴입니다.");
                    //this.$router.back();
                    this.logout();
                }
            },

            logout: async function () {
                axios.get("/api/user/logout");
                this.$router.push("/login");
            },
        }
    };
</script>
