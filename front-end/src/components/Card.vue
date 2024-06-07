<script setup>
defineProps({
    blog: {
        type: Object,
        required: true
    }
})
defineEmits(['titleClick'])
</script>

<template>
    <div>
        <h2 @click="$emit('titleClick', blog.title)">{{blog.title}}</h2>
        <p>{{blog.content}}</p>
        <a :href="blog.href">Read more</a>
        <slot></slot>
    </div>
</template>

<style scoped>

</style>