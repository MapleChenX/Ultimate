<script setup>
import {ref} from "vue";
const page = ref('1')
const pageSelect = (index) => {
    console.log(index)
    page.value = index
}
const search = ref('')
const userInfoToggle = ref(false)
const loginShowToggle = ref(false)
const loginToggle = () => {
    loginShowToggle.value = !loginShowToggle.value
}
const x = () => loginToggle()
const loginPanelShow = ref(true)
const loginPanelShowToggle = () => {
    loginPanelShow.value = !loginPanelShow.value
}
</script>

<template>
    <div>
        <div class="nav">
            <div class="nav-left liu">
                <div class="color-hover">首页</div>
                <div class="color-hover">论坛</div>
                <div class="color-hover">网盘</div>
                <div class="color-hover">聊天</div>
                <div class="color-hover">视觉</div>
            </div>
            <div class="nav-mid liu-mid">
                <el-input v-model="search" placeholder="请输入内容"></el-input>
                <el-button type="primary">
                    <el-icon>
                        <Search />
                    </el-icon>
                    搜索
                </el-button>

            </div>
            <div class="nav-right">
                <div class="liu" v-if="!userInfoToggle" style="min-height: 50px">
                    <div class="color-hover" @click="loginToggle">
                        登录
                    </div>
                </div>
                <div class="liu" v-else>
                    <div class="user-info">
                        <div class="color-hover">
                            用户名
                        </div>
                        <div class="color-hover">
                            邮箱
                        </div>
                    </div>

                    <div class="portrait color-hover" style="background-color: black">
                        <img src="@/assets/img/1.png" alt="" style="width: 50px;border-radius: 50%;">
                    </div>
                </div>
            </div>
        </div>

        <div class="loginCard" v-show="loginShowToggle" style="height: 400px;width: 600px;background-color: #ffffff;border-radius: 20px">
            <div class="x" @click="x">
                <el-icon><Close /></el-icon>
            </div>
            <div class="loginPanel" v-if="loginPanelShow">
                <el-input placeholder="请输入用户名"></el-input>
                <el-input placeholder="请输入密码"></el-input>
                <div style="display: flex;gap: 10px">
                    <el-input placeholder="验证码" style="flex: 1"></el-input>
                    <el-button placeholder="发送验证码" style="display: inline-block;width: 100px">发送验证码</el-button>
                </div>
                <el-button type="primary">登录</el-button>
                <span @click="loginPanelShowToggle">去注册</span>
            </div>
            <div class="registerPanel loginPanel" v-else>
                <el-input placeholder="请输入邮箱"></el-input>
                <el-input placeholder="请输入用户名"></el-input>
                <el-input placeholder="请输入密码"></el-input>
                <div style="display: flex;gap: 10px">
                    <el-input placeholder="验证码" style="flex: 1"></el-input>
                    <el-button placeholder="发送验证码" style="display: inline-block;width: 100px">发送验证码</el-button>
                </div>
                <el-button type="primary">注册</el-button>
                <span @click="loginPanelShowToggle">去登录</span>
            </div>
        </div>

        <div v-show="loginShowToggle" class="backdrop"></div>


        <div class="content">

        </div>

    </div>
</template>

<style lang="less" scoped>
.nav {
    display: flex;
    justify-content: space-between;
    padding: 2px 20px;
    border-bottom: 1px solid #ccc;
    position: sticky;
    top: 0;
    background-color: white; /* 添加背景色以防止其他内容在滚动时显示在.nav元素下方 */
    z-index: 1000; /* 可选：提高z-index以确保.nav元素在其他元素之上 */
}
.nav-left{
    display: flex;
    gap: 20px;
    align-items: center;
}
.liu{
    display: flex;
    align-items: center;
    gap: 20px;
}
.liu-mid{
    display: flex;
    align-items: center;
}
.color-hover:hover{
    color: #409EFF;
    cursor: pointer;
}
.loginCard {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 1002;
    display: flex;
    justify-content: center;
    align-items: center;
}
.backdrop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    backdrop-filter: blur(5px);
    z-index: 1001;
}
.x {
    position: absolute;
    top: 10px;
    right: 10px;
    &:hover {
        cursor: pointer;
    }
}
.loginPanel {
    display: flex;
    flex-direction: column;
    gap: 20px;
    padding: 20px;
    border-radius: 10px;
    background-color: white;
    //阴影
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>