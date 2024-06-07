<script setup>
import { ref } from 'vue';

// 创建一个响应式引用来存储图片的URL
const imageUrl = ref(null);

const form = new FormData();
form.append('prompt', 'a dog on beach');

fetch('https://clipdrop-api.co/text-to-image/v1', {
    method: 'POST',
    headers: {
        'x-api-key': '35feb8f0874217e6b187426957e959a250494d69b4413a2c44f7af8eab727b8d7a9c766a075f7a44147abd3d02839893',
    },
    body: form,
})
    .then(response => response.arrayBuffer())
    .then(buffer => {
        // Create a Blob from the ArrayBuffer
        const blob = new Blob([buffer], {type: 'image/png'});

        // Create a URL for the Blob
        const url = window.URL.createObjectURL(blob);

        // Update the imageUrl ref
        imageUrl.value = url;
    });
</script>

<template>
    <!-- 使用v-if确保只有当imageUrl有值时才显示图片 -->
    <img v-if="imageUrl" :src="imageUrl" alt="Generated Image"/>
</template>

<style scoped>

</style>