<script setup>
import {Terminal} from "xterm";
import "xterm/css/xterm.css";
import {AttachAddon} from "xterm-addon-attach/src/AttachAddon";
import {onBeforeUnmount, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";

const terminalRef = ref()

const socket = new WebSocket(`ws://127.0.0.1:8080/terminal`)

socket.onclose = evt => {
    if(evt.code !== 1000) {
        ElMessage.warning(`连接失败: ${evt.reason}`)
    } else {
        ElMessage.success('远程SSH连接已断开')
    }
}

// 终端本端
const term = new Terminal({
    lineHeight: 1.2,
    rows: 20,
    fontSize: 13,
    fontFamily: "Monaco, Menlo, Consolas, 'Courier New', monospace",
    fontWeight: "bold",
    theme: {
        background: '#000000'
    },
    // 光标闪烁
    cursorBlink: true,
    cursorStyle: 'underline',
    scrollback: 100, // 设置滚动缓冲区大小为0
    tabStopWidth: 4,
});

// 插件挂到socket上
const attachAddon = new AttachAddon(socket);
// 终端通过插件在元素上挂载显示
term.loadAddon(attachAddon);

onMounted(() => {
    // 页面一打开终端就打开
    term.open(terminalRef.value)
    term.focus()
})

onBeforeUnmount(() => {
    // 当页面一关闭，连接就关闭
    socket.close()
    // 终端也关闭
    term.dispose()
})

</script>

<template>
    <div ref="terminalRef" class="xterm" style="position: absolute;top: 50%;left: 50%;translate: -50% -50%;padding: 20px;border-radius: 15px;box-shadow: 0px 0px 10px rgb(26,204,231) " />
</template>


<style scoped>

</style>