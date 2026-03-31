// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { authService } from '@/services/auth/authService'
import { tokenManager } from '@/services/auth/tokenManager'

const routes = [
  {
    path: '/',
    name: 'welcome',
    component: () => import('@/views/WelcomePage.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/callback',
    name: 'callback',
    component: () => import('@/views/CallbackPage.vue')
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('@/views/home/HomePage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'admin-dashboard',
        component: () => import('@/views/admin/AdminDashboard.vue')
      },
      {
        path: 'categories',
        name: 'admin-categories',
        component: () => import('@/views/admin/AdminCategories.vue')
      }
    ]
  },
  {
    path: '/trade',
    component: () => import('@/views/trade/TradeProfileLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'settings',
        name: 'trade-settings',
        component: () => import('@/views/trade/TradeSettings.vue')
      },
      {
        path: 'drafts',
        name: 'trade-drafts',
        component: () => import('@/views/trade/DraftsList.vue')
      },
      {
        path: 'published',
        name: 'trade-published',
        component: () => import('@/views/trade/PublishedList.vue')
      },
      {
        path: 'closed',
        name: 'trade-closed',
        component: () => import('@/views/trade/ClosedList.vue')
      },
      {
        path: 'bulletin/edit/new',
        name: 'bulletin-create',
        component: () => import('@/views/bulletin/BulletinEditPage.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'bulletin/edit/:id',
        name: 'bulletin-edit',
        component: () => import('@/views/bulletin/BulletinEditPage.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/chat',
    component: () => import('@/views/chat/components/layout/ChatLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'settings',
        name: 'chat-settings',
        component: () => import('@/views/chat/ChatSettings.vue')
      },
      {
        path: 'contacts',
        name: 'chat-contacts',
        component: () => import('@/views/chat/ChatContacts.vue')
      },
      {
        path: 'search',
        name: 'chat-search',
        component: () => import('@/views/chat/ChatSearch.vue')
      },
      {
        path: '/chat/room/:id',
        name: 'chat-room',
        component: () => import('@/views/chat/ChatRoom.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/bulletin/view/:id',
    name: 'bulletin-view',
    component: () => import('@/views/bulletin/BulletinViewPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/search',
    name: 'bulletin-search',
    component: () => import('@/views/bulletin/SearchResultsPage.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Проверка на админа
const isAdmin = () => {
  const token = tokenManager.getAccessToken()
  if (!token) return false
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    const roles = payload.spring_sec_roles || payload.realm_access?.roles || []
    return roles.includes('admin') || roles.includes('ROLE_admin') || roles.includes('ADMIN')
  } catch (e) {
    return false
  }
}

router.beforeEach((to) => {
  const isAuthenticated = tokenManager.isAuthenticated()
  const isAnonymous = authService.isAnonymous()
  const hasSession = isAuthenticated || isAnonymous

  if (to.path === '/callback') {
    return true
  }

  if (to.meta.requiresAdmin) {
    if (!isAuthenticated) {
      return '/'
    }
    if (!isAdmin()) {
      return '/home'
    }
    return true
  }

  if (to.meta.requiresGuest && hasSession) {
    return '/home'
  }

  return true
})

export default router