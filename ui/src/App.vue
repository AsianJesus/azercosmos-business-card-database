<template>
    <div id="app" class="container-fluid">
        <!--<div class="bcards-header row">-->
        <!--&lt;!&ndash;<div class="col bcards-header-holder">-->
        <!--<router-link :to="{name: 'BusinessCards'}" class="bcards-header-nav">Main page</router-link>-->
        <!--</div>-->
        <!--<div class="col bcards-header-holder">-->
        <!--<router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'private'}}" class="bcards-header-nav">-->
        <!--Private-->
        <!--</router-link>-->
        <!--</div>&ndash;&gt;-->
        <!--<div class="col bcards-header-holder">-->
        <!--<router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'public'}}" class="bcards-header-nav">-->
        <!--Public-->
        <!--</router-link>-->
        <!--</div>-->
        <!--<div class="col bcards-header-holder">-->
        <!--<router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'mycards'}}" class="bcards-header-nav">-->
        <!--My cards-->
        <!--</router-link>-->
        <!--</div>-->
        <!--&lt;!&ndash;<div class="col bcards-header-holder">-->
        <!--<router-link :to="{name: 'NewBusinessCard'}" class="bcards-header-nav">-->
        <!--New card-->
        <!--</router-link>-->
        <!--</div>&ndash;&gt;-->
        <!--</div>-->

        <header class="header-class">
            <nav class="navbar navbar navbar-light navbar-expand-md">
                <div id="collapse" class="nav-collapse navbar-collapse collapse" style="display: none;">
                    <ul class="navbar-nav">
                        <!--<li class="nav-link router-link-exact-active router-link-active active-module" id="link">-->
                        <!--<router-link :to="{name: 'BusinessCards'}"-->
                        <!--id="link"-->
                        <!--class="nav-link"-->
                        <!--tag="li"-->
                        <!--&gt;-->
                        <!--Main Page-->
                        <!--</router-link>-->
                        <!--<router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'private'}}"-->
                        <!--id="link"-->
                        <!--class="nav-link"-->
                        <!--tag="li"-->
                        <!--&gt;-->
                        <!--Private-->
                        <!--</router-link>-->
                        <router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'public'}}"
                                     id="link"
                                     class="nav-link"
                                     tag="li"
                        >
                            Public
                        </router-link>
                        <router-link :to="{name: 'BusinessCardsWithOption', params: {option: 'mycards'}}"
                                     id="link"
                                     class="nav-link"
                                     tag="li"
                        >
                            My cards
                        </router-link>
                        <!--<router-link :to="{name: 'NewBusinessCard'}"-->
                        <!--id="link"-->
                        <!--class="nav-link"-->
                        <!--tag="li"-->
                        <!--&gt;-->
                        <!--New Card-->
                        <!--</router-link>-->

                    </ul>
                </div>
            </nav>
        </header>
        <div class="row">
            <div class="col-12">
                <router-view></router-view>
            </div>
        </div>
    </div>
</template>

<script>
import Toolbar from './components/Toolbar/ToolbarComponent.vue'
export default {
  name: 'App',
  components: {
    Toolbar
  },
  created () {
    this.loadUserInfo()
    document.title = this.$route.meta.title
  },
  watch: {
    $route () {
      document.title = this.$route.meta.title
    }
  },
  methods: {
    loadUserInfo () {
      this.axios.get('/user').then(response => {
        this.$store.commit('updateUserInfo', response.data)
      })
    }
  }
}
</script>

<style>
    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }

    .bcards-header {
        background-color: #17a2b8;
    }

    .bcards-header-nav {
        color: white !important;
        text-decoration: none;
    }

    .bcards-header-holder {
        font-size: 1.1rem;
        padding-top: .2rem;
        padding-bottom: .2rem;
        transition: .2s ease;
    }

    .bcards-header-holder:hover {
        background: #30303030;

    }


    .unread {
        background-color: #d6d8db;
    }

    .nots {
        position: absolute;
        top: 15px;
        right: 25px;
    }

    .not-icon {
        width: 18px;
        color: white;
        cursor: pointer;
    }

    .not-counts {
        display: inline-block;
        position: absolute;
        top: -10px;
        right: -18px;
        width: 20px;
        height: 20px;
        background-color: #fff;
        border: 2px solid #7c7c7c;
        border-radius: 100px;
        color: #7c7c7c;
        font-size: 11px;
    }

    .not-dropdown-block {
        position: absolute;
        background-color: white;
        right: -26px;
        width: 300px;
        border: 1px solid #777;
        z-index: 9999;
        border-radius: 10px;
    }

    .fade-enter-active, .fade-leave-active {
        transition: opacity .3s;
    }

    .fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */
    {
        opacity: 0;
    }

    .not-dropdown-item {
        padding: 5px 10px;
        text-align: left;
        font-size: 14px;
    }

    .not-dropdown-item:not(:last-child) {
        border-bottom: 1px solid #aaa;
        border-radius: 10px;
    }

    .not-view-all {
        color: blue;
        text-decoration: underline;
        padding: 5px;
        font-size: 14px;
        cursor: pointer;
    }

    .router-link-exact-active {
        text-decoration: underline;
    }

    .nav-collapse {
        justify-content: space-between;
    }

    .seach-input {
        width: 400px !important;
    }

    #collapse {
        align-items: center;
        justify-content: center;
    }

    #link {
        margin: 0 5px;
        cursor: pointer;
        color: #7c7c7c;
        font-weight: 700;
    }

    .active-module {
        text-decoration: underline;
    }

    .navbar {
        color: #7c7c7c;
        box-shadow: 0 0 3px 3px #30303020 inset;
    }
</style>
