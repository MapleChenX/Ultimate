<template>
    <div>
        <input type="file" @change="onFileChange" />
        <button @click="upload">Upload</button>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            selectedFile: null
        };
    },
    methods: {
        onFileChange(e) {
            this.selectedFile = e.target.files[0];
        },
        async upload() {
            if (!this.selectedFile) {
                return;
            }

            const formData = new FormData();
            formData.append('file', this.selectedFile);

            try {
                const response = await axios.post('http://localhost:8080/video/upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                console.log('Upload successful:', response);
            } catch (error) {
                console.error('Failed to upload file:', error);
            }
        }
    }
};
</script>