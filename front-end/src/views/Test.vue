<script setup>
import {computed, onMounted, ref} from "vue";
import Card from "../components/Card.vue";
import { useWebStore } from "../stores/index.js";
const use = useWebStore();

const blog = ref([{
    id: 1,
    title: "This is 1 title",
    content: "This is 1 content",
    href: "https://www.google.com"
},{
    id: 2,
    title: "This is 2 title",
    content: "This is 2 content",
    href: "https://www.google.com"
},{
    id: 3,
    title: "This is 3 title",
    content: "This is 3 content",
    href: "https://www.google.com"
}])

// blog发生变化时，total会动态更新
const total = computed(() => {
    return blog.value.length
})

const show = ref(true)
const toggle = () => {
    show.value = !show.value
}
const newBlog = ref({
    title: "",
    content: "",
    href: ""
})
const addBlog = () => {
    blog.value.push({
        id: blog.value.length + 1,
        ...newBlog.value
    })
    newBlog.value = {
        title: "",
        content: "",
        href: ""
    }
}

const countAll = ref(0)

onMounted(() => {
    setInterval(() => {
        countAll.value += 999
    }, 2000)
})

const c = (t) => {
    console.log("title clicked")
    console.log(t)
}
</script>

<template>
    <hr>
    <div>
        <div>{{ useWebStore().num }}</div>
    </div>
    <hr>
    <h1>总浏览量 {{ countAll }}</h1>
    <h2>{{blog[0].title}}</h2>
    <p>{{blog[0].content}}</p>
    <a :href="blog[0].href">Read more</a>
    <div v-if="blog[0].content.length > 1">文章长度大于5
        <button @click="blog[0].content = 0">清空</button>
    </div>
    <div style="height: 100px"></div>

    <Card @titleClick="c" v-for="b in blog" :blog="b">
        <button>欧耶</button>
    </Card>

    <h3 v-if="show">总共有{{ blog.length }}文章</h3>
    <button @click="toggle">{{ show ? "隐藏" : "显示" }}文章总数</button>

<!--    用户添加文章-->
    <div>
        <input type="text" placeholder="请输入标题" v-model="newBlog.title">
        <input type="text" placeholder="请输入内容" v-model="newBlog.content">
        <input type="text" placeholder="请输入链接" v-model="newBlog.href">
        <button @click="addBlog">添加文章</button>
    </div>

    <h3>按钮</h3>
    <el-button>默认按钮</el-button>
    <el-button type="primary">主要按钮</el-button>
    <el-button type="success">成功按钮</el-button>
    <el-button type="info">信息按钮</el-button>
    <el-button type="warning">警告按钮</el-button>
    <el-button type="danger">危险按钮</el-button>

    <hr>
    <h3>按钮属性</h3>
    <el-button plain type="primary">朴素按钮</el-button>
    <el-button round type="primary">圆角按钮</el-button>
    <el-button circle type="primary">圆</el-button>
    <el-button disabled type="primary">禁用按钮</el-button>
    <el-button loading type="primary">加载中</el-button>

    <hr>
    <h3>尺寸</h3>
    <el-button size="large">大型按钮</el-button>
    <el-button>默认按钮</el-button>
    <el-button size="small">小型按钮</el-button>

    <div class="bg" style="background-color: black">
        <el-button plain class="custom-button">自定义颜色的按钮</el-button>
        <button class="c">ds</button>
    </div>

</template>

<style scoped>
/* 清除a标签的样式 */
a{
    text-decoration: none;
}
.custom-button {
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(5px);
    border: none;
    color: #000;
}
.c{
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(5px);
    border: none;
    color: #000;
    border-radius: 5px;
}
</style>