import { defineStore } from "pinia";
import {ref, reactive, computed} from "vue";
import axios from "axios";

export const useWebStore = defineStore("web", () => {
    const user = reactive({
        avatarPath: ""
    })

    const avatarUrl = computed(() => {
        if(user.avatarPath)
            return `${axios.defaults.baseURL}/v1/file/avatarGet${user.avatarPath}`
        else
            return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    })

    return { user, avatarUrl };
}, {
    persist: true
})