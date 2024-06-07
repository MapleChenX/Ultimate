<script setup>

import Son from "./Son.vue";
import Daughter from "./Daughter.vue";
import { useWebStore } from "../stores/index.js";
const use = useWebStore();

</script>

<template>
    <div>this is father</div>
    <Son>
        <div>slot</div>
    </Son>
    <Daughter>
        <div>this is no name</div>
        <template #what="data">
            {{data.title}}<br>
            {{data.content}}
            <div>this is what from father</div>
        </template>
    </Daughter>
    <hr>

    <div>{{ useWebStore().num }}</div>

    <div>
        <button @click="useWebStore().add()">add</button>
    </div>
</template>

<style scoped>

</style>