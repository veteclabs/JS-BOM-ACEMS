<template>
    <div id="settingNav">
        <ul>
            <li v-for="(item, index) in menus" :key="index">
                <ul>
                    <li v-if="item.subMenu">
                        <ul>
                            <li v-for="subItem in item.subMenu">
                                <nuxt-link :to="subItem.link" title="subItem.menuName">
                                        <img :src="require(`@/assets/images/setting/${subItem.icon}.png`)" width="24"
                                             v-if="subItem.icon"/>
                                    {{subItem.menuName}}
                                </nuxt-link>
                            </li>
                        </ul>
                    </li>
                    <li v-else>
                        <nuxt-link :to="item.link" title="subItem.menuName">
                            <img :src="require(`@/assets/images/setting/${item.icon}.png`)" width="24"
                                 v-if="item.icon"/>
                            {{item.menuName}}
                        </nuxt-link>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</template>
<script>
  import allMenuArray from '~/assets/data/allMenuArray.json';

  export default {
    name: 'settingNav',
    data() {
      return {
        path: '',
        menus: '',
      };
    },
    created() {
      this.menuSetting();
    },
    methods: {
      menuSetting() {
        this.menus = allMenuArray.list;
        let tempSettingMenu;
        for (let i in allMenuArray.list) {
          if (allMenuArray.list[i].class === 'setting') {
            tempSettingMenu = allMenuArray.list[i].subMenu;
          }
        }
        this.menus = tempSettingMenu;
        this.menuPathChk();
      },
      menuPathChk() {
        this.path = this.$route.path;
      },
    },
    watch: {
      $route() {
        this.menuPathChk();
      },
    },
    filters: {
      toUpperCase: function (value) {
        return value.toUpperCase();
      }
    }
  };
</script>
