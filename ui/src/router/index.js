import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import BusinessCardsPage from '@/components/Business cards/BusinessCardsPage'
import NewBusinessCard from '@/components/Business cards/NewBusinessCard.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'BusinessCards',
      component: BusinessCardsPage
    },
    {
      path: '/new-business-card',
      name: 'NewBusinessCard',
      component: NewBusinessCard
    }
  ]
})
