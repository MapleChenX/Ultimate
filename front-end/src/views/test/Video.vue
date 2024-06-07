<script setup>
import { ref, onMounted } from 'vue';
import Hls from 'hls.js';

const videoRef = ref(null);
const videoUrl = 'http://localhost:8080/v1/file/video/m3u8?fileId=ea5bcb63cd0687b6dd0da9ce6702f863';
onMounted(() => {
    const hls = new Hls();
    hls.loadSource(videoUrl);
    hls.attachMedia(videoRef.value);
});
</script>
<template>
    <div style="height: 100%; display: flex;justify-content: center;align-items: center">
        <video ref="videoRef" style="height: 500px" controls></video>
    </div>
</template>

<style scoped>

</style>