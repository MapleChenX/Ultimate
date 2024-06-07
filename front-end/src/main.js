import { createApp } from 'vue'
import router from './router/index.js'
import App from './App.vue'
//整体导入 ElementPlus 组件库
import ElementPlus from 'element-plus' //导入 ElementPlus 组件库的所有模块和功能
import 'element-plus/dist/index.css' //导入 ElementPlus 组件库所需的全局 CSS 样式
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from "axios"; //导入 ElementPlus 组件库中的所有图标

axios.defaults.baseURL = 'http://localhost:8080'

const app = createApp(App)
const pinia = createPinia()

//注册 ElementPlus 组件库中的所有图标到全局 Vue 应用中
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

pinia.use(piniaPluginPersistedstate)
app.use(router)
app.use(ElementPlus)
app.use(pinia)


app.mount('#app')
