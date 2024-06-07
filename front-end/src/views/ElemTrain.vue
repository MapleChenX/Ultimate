<script setup>
import {ref} from "vue";
import {Search} from "@element-plus/icons-vue";
import {ElMessage, ElMessageBox, ElNotification} from "element-plus";
import router from "@/router/index.js";

let a = ref("a")
const change = () => {
    a.value +='a'
    console.log('click')
    console.log(a.value)
}

// 消息
const openMsg = () => {
    ElMessage({
        type: 'success',
        message: '恭喜你，这是一条成功消息'
    })
}

const confirmMsg = () => {
    ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        ElMessage({
            type: 'success',
            message: '删除成功!'
        });
    }).catch(() => {
        ElMessage({
            type: 'info',
            message: '已取消删除'
        });
    });
}

const openNotifiy = () => {
    ElNotification({
        type: 'success', // success | warning | info | error
        title: '标题',
        message: 'dengruicode.com',
        duration: 2000
    })
}

const selectedIndex = ref('2')
const selected = (index) => {
    console.log(index)
    selectedIndex.value = index
    if(index === '7'){
        // 路由跳转到home页面
        router.push('/father')
    }
}
</script>

<template>
    <div>
        <h3>垂直导航-默认展开和自定义样式</h3>
        <el-menu :default-active="selectedIndex" @select="selected" :default-openeds="defaultOpeneds"
                 background-color="#545c64" text-color="#fff" active-text-color="#ffd04b" style="width: 200px;">
            <el-sub-menu index="1">
                <template #title>
                    <el-icon><Search /></el-icon>
                    <span>导航一</span>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                </el-menu-item-group>
            </el-sub-menu>
            <el-menu-item index="2">
                <el-icon><Edit /></el-icon>
                <template #title>导航二</template>
            </el-menu-item>
            <el-sub-menu index="3">
                <template #title>
                    <el-icon><Search /></el-icon>
                    <span>导航三</span>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="3-1">选项1</el-menu-item>
                    <el-menu-item index="3-2">选项2</el-menu-item>
                </el-menu-item-group>
            </el-sub-menu>
            <el-menu-item index="4">
                <el-icon><Setting /></el-icon>
                <template #title>导航四</template>
            </el-menu-item>
        </el-menu>

        <h1>ElemTrain</h1>

        <hr>
        <div>
            <h3>按钮</h3>
            <div @click="change" class="button" style="display: inline-block;border-radius: 5px;padding: 5px;border: 2px solid red">click me</div>
            <br>
            <el-button type="primary" round plain>
                <el-icon> <search/> </el-icon>
                fuck
            </el-button>

        </div>

        <h3>图标</h3>
        <el-button-group>
            <el-button type="primary"  >
                <el-icon> <search/> </el-icon>
                fuck
            </el-button>
            <el-button style="margin-left: 5px;margin-right: 5px" type="primary"  >
                <el-icon> <search/> </el-icon>
                you
            </el-button>
            <el-button type="primary"  >
                <el-icon> <search/> </el-icon>
                bitch
            </el-button>
        </el-button-group>
        <hr>
        <el-button @click="openMsg">msg</el-button>
        <el-button @click="confirmMsg">msg</el-button>
        <el-button @click="openNotifiy">msg</el-button>
    </div>
</template>

<style scoped>



</style>