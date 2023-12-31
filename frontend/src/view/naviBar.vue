<template>
  <div>
    <!-- 顶栏 -->
    <v-app-bar app elevation="0" :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
      <v-app-bar-nav-icon @click="miniManual = !miniManual" class="hidden-sm-and-down"></v-app-bar-nav-icon>
      <v-toolbar-title>{{ $router.currentRoute.meta.title }}</v-toolbar-title>
      <v-spacer></v-spacer>

      <v-menu offset-y allow-overflow min-width="400">
        <template v-slot:activator="{ on, attrs }">
          <v-badge :value="unReadNum" :content="unReadNum" overlap bordered dot color="error">
            <v-btn icon v-bind="attrs" v-on="on">
              <v-icon>mdi-bell-outline</v-icon>
            </v-btn>
          </v-badge>

        </template>

        <v-list :color="$vuetify.theme.dark === false ? 'white' : '#121212'" v-if="notices.length !== 0 && ready">
          <v-list-item-title class="text-h5 ma-4">通知</v-list-item-title>
          <v-divider class="mt-2"></v-divider>

          <v-virtual-scroll bench="10" :items="notices" :item-height="63" height="400" width="400">
            <template v-slot:default="{ item }">

              <v-list-item @click="check()">
                <v-list-item-avatar>
                  <v-img size="70" :src="item.avatar"></v-img>
                </v-list-item-avatar>

                <v-list-item-content @click="check()">
                  <v-list-item-title :class="item.isRead === false ? 'strong--text' : 'grey--text'">
                    {{ messages[item.type] }}
                  </v-list-item-title>
                  <v-list-item-subtitle>
                    {{ item.createAt }}
                  </v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
              <v-divider></v-divider>

            </template>
          </v-virtual-scroll>

          <v-subheader><v-icon>mdi-menu-right</v-icon> <router-link to="/controlpanel/notifications"
              style="text-decoration:none">进入通知中心查看全部消息</router-link></v-subheader>
        </v-list>

        <v-sheet v-else-if="!ready">
          <v-container style="height:550px;">
            <v-row class="fill-height" align-content="center" justify="center">
              <v-col class="text-h5 text-center" cols="12">
                正在加载通知
              </v-col>
              <v-col cols="6">
                <v-progress-linear color="primary accent-4" indeterminate rounded height="6"></v-progress-linear>
              </v-col>
            </v-row>
          </v-container>
        </v-sheet>

        <v-sheet v-else>
          <v-container style="height: 550px;">
            <v-row class="fill-height" align-content="center" justify="center">
              <v-col class="text-h5 text-center" cols="12">
                没有通知
              </v-col>
            </v-row>
          </v-container>
        </v-sheet>



      </v-menu>

      <v-menu bottom min-width="200" rounded class="ma-6" offset-y>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" class="ma-8">
            <v-avatar size="44">
              <v-img :src="user.avatar"></v-img>
            </v-avatar>
          </v-btn>
        </template>
        <v-card>
          <v-list-item-content class="justify-center">
            <div class="mx-auto text-center">
              <v-avatar size="80">
                <v-img :src="user.avatar"></v-img>
              </v-avatar>
              <h3 class="mt-2">{{ user.userName }}</h3>
              <p class="text-caption mt-1">
                {{ user.email }}
              </p>
              <v-divider class="my-3"></v-divider>
              <v-btn depressed rounded text to="settings">
                账户设置
              </v-btn>
              <v-divider class="my-3"></v-divider>
              <v-btn depressed rounded text color="warning" @click="logout()">
                退出登录
              </v-btn>
            </div>
          </v-list-item-content>
        </v-card>
      </v-menu>

    </v-app-bar>

    <!-- 抽屉 -->
    <v-navigation-drawer v-model="drawer" permanent :mini-variant="mini" app mini-variant-width="60" width="220"
      :color="$vuetify.theme.dark === false ? 'white' : '#151515'">

      <v-list-item class="px-2 mt-2">
        <v-list-item v-if="!mini" to="/">
          <v-img v-if="$vuetify.theme.dark === false" :src="require('../assets/logo-md.png')" width="10"></v-img>
          <v-img v-else :src="require('../assets/logo-md-dark.png')" width="10"></v-img>
        </v-list-item>

        <v-avatar v-if="mini" @click="$router.push('/')">
          <v-img :src="require('../assets/logo-sm.png')"></v-img>
        </v-avatar>
      </v-list-item>

      <v-list shaped class="mt-5 text-left">

        <v-list-item-group v-model="selectedItem" color="primary" mandatory>

          <v-list-item link to="dashboard">
            <v-list-item-icon>
              <v-icon>mdi-home</v-icon>
            </v-list-item-icon>
            <v-list-item-title>概览</v-list-item-title>
          </v-list-item>

          <v-list-item link to="arrange">
            <v-list-item-icon>
              <v-icon>mdi-sitemap</v-icon>
            </v-list-item-icon>
            <v-list-item-title>智能排班</v-list-item-title>
          </v-list-item>


          <v-list-item to="absences" link v-if="$store.state.isManager || $store.state.isShopManager">
            <v-list-item-icon>
              <v-icon>mdi-shore</v-icon>
            </v-list-item-icon>
            <v-list-item-title>请假管理</v-list-item-title>
          </v-list-item>

          <v-list-item to="notifications" link>
            <v-list-item-icon>
              <v-icon>{{ unReadNum === 0 ? 'mdi-bell' : 'mdi-bell-badge' }}</v-icon>
            </v-list-item-icon>
            <v-list-item-title>通知中心</v-list-item-title>
          </v-list-item>

          <v-list-group prepend-icon="mdi-inbox" mandatory :value="expand"
            v-if="$store.state.isManager || $store.state.isShopManager">
            <template v-slot:activator>
              <v-list-item-title>信息管理</v-list-item-title>
            </template>

            <v-list-item to="branches" link>
              <v-list-item-icon>
                <v-icon>mdi-store</v-icon>
              </v-list-item-icon>
              <v-list-item-title>分店信息</v-list-item-title>
            </v-list-item>

            <v-list-item to="staff" link>
              <v-list-item-icon>
                <v-icon>mdi-account</v-icon>
              </v-list-item-icon>
              <v-list-item-title>员工信息</v-list-item-title>
            </v-list-item>
          </v-list-group>

          <v-list-item to="reviews" link v-if="$store.state.isManager || $store.state.isShopManager">
            <v-list-item-icon>
              <v-icon>mdi-message-processing</v-icon>
            </v-list-item-icon>
            <v-list-item-title>顾客评价</v-list-item-title>
          </v-list-item>

          <v-list-item to="settings" link>
            <v-list-item-icon>
              <v-icon>mdi-cogs</v-icon>
            </v-list-item-icon>
            <v-list-item-title>用户设置</v-list-item-title>
          </v-list-item>

        </v-list-item-group>

      </v-list>

      <v-footer absolute v-if="mini === false" :color="$vuetify.theme.dark === false ? '' : '#1b1b1b'">
        <v-subheader class="grey--text font-italic">©2023</v-subheader>
      </v-footer>
    </v-navigation-drawer>

    <!-- 内容显示区 -->
    <v-main>
      <router-view @msg="getMsg"></router-view>
    </v-main>

    <v-snackbar v-model="snackBar" style="z-index: 1001;">
      {{ snackBarText }}
      <template v-slot:action="{ attrs }">
        <v-btn color="error" icon v-bind="attrs" @click="snackBar = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import { getUserAvatar, getUserInfo, logout } from '@/request/user'
import { getNotisByCount } from '@/request/notis'
import { formatDate } from '@/plugins/utility'

export default {
  data: () => ({
    snackBar: false,
    snackBarText: '',
    drawer: false,
    miniManual: false,
    ready: false,

    user: {
      avatar: '',
      userName: '',
      email: '',
    },

    notices: [{}],
    noticeAvatars: {},

    messages: {
      1: '发布了一个新的排班表',
      2: '变更了排班',
      3: '有一个开放班次长时间无人认领，点击进行手动排班',
      4: '申请了请假',
      5: '请假审核状态更新'
    }



  }),
  computed: {

    mini() {
      return this.$vuetify.breakpoint.smAndDown ? true : this.miniManual
    },
    selectedItem: {
      set() { },
      get() {
        return this.$router.currentRoute.meta.selectedItem
      }
    },
    expand() {
      let path = this.$router.currentRoute.meta.selectedItem
      return path > 4
    },
    unReadNum() {
      let num = 0;
      for (const notice in this.notices) {
        if (this.notices[notice].isRead === false) {
          num++
        }
      }
      return num
    }
  },
  methods: {
    darkMode() {
      localStorage.dark = localStorage.dark === 'true' ? '' : 'true'
      this.$vuetify.theme.dark = localStorage.dark
    },

    logout() {
      logout().then(res => {
        if (res.data.code === 0) {
          this.$store.commit('deleteLoginInfo')
          this.getMsg('退出登录成功，正在重定向...')
          this.$router.push('/')
        }
        else {
          this.getMsg('退出登录失败')
        }
      }).catch(
        this.getMsg('网络错误')
      )
    },

    getMsg(data) {
      this.snackBarText = data
      this.snackBar = true
    },
    check() {
    },


  },

  mounted() {
    getUserInfo().then(res => {
      if (res.data.code === 0) {
        this.user.userName = res.data.data.username
        this.user.email = res.data.data.email
      }

    }).catch(() => {
      this.getMsg('网络错误')
    })

    getUserAvatar(this.$store.state.userId).then(res => {
      if (res.status === 200) {
        this.user.avatar = URL.createObjectURL(res.data)
      }
      else if (res.status === 204) {
        this.user.avatar = require('../assets/defaultAvatar.png')
      }
    }).catch(() => {
      this.getMsg('网络错误')
    })

    getNotisByCount(10).then(async res => {
      const notices = res.data.data;
      for (const notice of notices) {
        notice.avatar = require('../assets/defaultAvatar.png')
        let time = new Date(notice.createAt)
        notice.createAt = formatDate(notice.createAt) + ' ' + (time.getHours() < 10 ? '0' + time.getHours() : time.getHours()) + ':' + (time.getMinutes() < 10 ? '0' + time.getMinutes() : time.getMinutes())
      }
      this.notices = notices
      if (notices.length === 0) this.notices = []
      this.ready = true

      for (const notice of notices) {
        const avatar = await getUserAvatar(notice.fromUser);
        if (avatar.status === 200) {
          notice.avatar = URL.createObjectURL(avatar.data)
        }
      }

    }).catch(() => {
      this.$emit('msg', '网络错误')
    })
  }



}
</script>

