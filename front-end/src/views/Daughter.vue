<script setup>

</script>

<template>
    <div>this is daughter</div>
    <hr>
    <slot></slot>
    <hr>
    <div>
        <div>what slot</div>
        <slot name="what" title="nothing" content="nothing to say"></slot>
    </div>

</template>

<style scoped>

</style>