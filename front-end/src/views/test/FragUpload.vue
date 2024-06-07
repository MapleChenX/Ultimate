<script setup>
import { ref } from 'vue';
import axios from 'axios';
import SparkMD5 from 'spark-md5';

const file = ref(null); //文件对象
const chunkSize1 = 1024 * 1024 * 10; // 分片大小，这里设置为 10MB
const chunkSize = 1024 * 1024 * 100; // 分片大小，这里设置为 100MB
const chunkSize3 = 1024 * 1024 * 500; // 分片大小，这里设置为 500MB

const onFileChange = (e) => {
    file.value = e.target.files[0]; // e.target 是触发事件的元素，也就是 <input type="file" /> 元素; e.target.files 是文件列表
};


function computeFileHash(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        const spark = new SparkMD5.ArrayBuffer();
        reader.readAsArrayBuffer(file);
        reader.onload = (event) => {
            spark.append(event.target.result);
            resolve(spark.end());
        };
        reader.onerror = (error) => {
            reject(error);
        };
    });
}

// 分片上传
const upload = async () => {
    const totalChunks = Math.ceil(file.value.size / chunkSize); // 总分片数，向上取整
    const uploadPromises = [];
    let md5 = '';
    await computeFileHash(file.value).then(hash => {
        md5 = hash;
        console.log(md5); // 输出文件的 MD5 值
        console.log(typeof md5);
    });

    // 分片从0开始
    for (let i = 0; i < totalChunks; i++) {
        const start = i * chunkSize; // 分片的起始位置
        const end = Math.min(file.value.size, start + chunkSize); // 分片的结束位置
        const chunk = file.value.slice(start, end); // 从文件中截取分片

        const formData = new FormData();
        formData.append('file', chunk);
        formData.append('filename', file.value.name);
        formData.append('chunkIndex', i);
        formData.append('totalChunks', totalChunks);
        formData.append('md5', md5);  // 添加文件的 MD5 值

        // 这里没有await，因为我们希望所有分片并行上传
        const promise = axios.post('http://localhost:8080/v1/file/uploadChunk', formData);
        uploadPromises.push(promise);
    }

    // 等待获取所有结果，即所有分片上传完成，才会执行后续代码
    await Promise.all(uploadPromises);
    //
    // // 所有分片上传完成后，通知服务器合并分片
    // await axios.post('/your/api/for/merge', {
    //     filename: file.value.name,
    //     totalChunks: totalChunks,
    //     fileHash: fileHash  // 添加文件的 MD5 值
    // });
};
</script>

<template>
    <input type="file" @change="onFileChange" />
    <button @click="upload">Upload</button>
</template>