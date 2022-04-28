<template>
    <div v-if="propsdata">
        <ul class="tag-box">
            <li>
                <div class="tagname">그룹제어</div>
                <div>
                    {{propsdata.groupName}}
                    <div v-if="$options.filters.pickGroup(groupList,'id', propsdata.groupId) === true"
                         class="bom-badge blue-badge blue">ON</div>
                    <div v-else class="bom-badge red-badge red">OFF</div>
                </div>
            </li>
            <li>
                <div class="tagname">개별제어</div>
                <div v-if="propsdata.isActive" class="bom-badge blue-badge blue">ON</div>
                <div v-if="!propsdata.isActive" class="bom-badge red-badge red">OFF</div>
            </li>
        </ul>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        props: ['propsdata'],
        components: {
            axios,
        },
        data() {
            return {
                groupList:[],
            }
        },
        mounted() {
            this.getGroup();
        },
        methods: {
            getGroup() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/groups',
                    headers: {
                        setting: true,
                    }
                }).then((res) => {
                    vm.groupList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                });
            },
        },
        filters: {
            pickGroup: function (object, property, value) {
                if (object === undefined || object === null || object === "") {
                    return -1;
                } else {
                    let target = object.filter(object => object[property] === value);
                    if (target.length === 0) {
                        return -100;
                    } else {
                        return target[0].schedule.isActive;
                    }
                }
            },
        }
    };
</script>
