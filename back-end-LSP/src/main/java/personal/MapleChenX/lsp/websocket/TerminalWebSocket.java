package personal.MapleChenX.lsp.websocket;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// 在onOpen阶段，前端和服务端的连接已经建立好了，是可以给前端发送数据了的
// 而且因为我们在onOpen阶段还建立了服务端和远程主机的连接，此时服务端具备了向前端发送数据的能力，而和远程主机甚至可以直接交互了
// onOpen之后的情况是：三端之间的四连接已经建立起了三个，只差[前端->服务端]这个连接了
// 而onMessage就是用来完成这最后一个连接的；到此为止，四连接全部建立完毕，三端可以随意通信了

// 虽然在onOpen阶段有了三个方向的输出，但是只用了两个。分别是: [远程->服务端]、[服务端->前端]
// 而[前端->服务端]、[服务端->远程]是在onMessage阶段才会用到的

// xterm终端附加到WebSocket
@Slf4j
@Component
@ServerEndpoint("/terminal")
public class TerminalWebSocket {
    private static final Map<Session, Shell> sessionMap = new ConcurrentHashMap<>();
    // 处理远程到前端
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    @OnOpen
    public void onOpen(Session session) throws Exception {
        // 前端通过websocket连接到后端，后端再连接到远程主机
        // 一个前端与服务端的session，对应一个服务端与远程主机的连接
        // 参数1：前端与服务端的session，参数2+3：远程主机的：地址、端口、账号、密码
        Host host = new Host();
        if(this.createSshConnection(session, host)) {
            log.info("主机 {} 的SSH连接已创建", host.getIp());
        }
    }

    // todo 0.2 前端到远程主机
    // 处理前端到远程
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // 获取远程主机输出流
        Shell shell = sessionMap.get(session);
        OutputStream output = shell.output;
        // 把前端传来的数据发送到远程主机
        output.write(message.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        Shell shell = sessionMap.get(session);
        if(shell != null) {
            shell.close();
            sessionMap.remove(session);
            log.info("主机 {} 的SSH连接已断开", shell.js.getHost());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        log.error("用户WebSocket连接出现错误", error);
        session.close();
    }

    // 建立一个SSH连接，并打开一个shell通道，以便执行命令
    private boolean createSshConnection(Session session, Host host) throws IOException{
        try {
            JSch jSch = new JSch();
            // 远程账号密码
            com.jcraft.jsch.Session js = jSch.getSession(host.getUsername(), host.getIp(), host.getPort());
            js.setPassword(host.getPassword());
            js.setConfig("StrictHostKeyChecking", "no");
            js.setTimeout(30000);
            // SSH连接
            js.connect();

            // 在SSH会话已经建立的情况下，我们可以打开多个通道来执行不同的操作
            ChannelShell channel = (ChannelShell) js.openChannel("shell");

            // 设置伪终端的类型为"xterm"。伪终端是一种模拟物理终端的设备，它可以处理用户的输入和输出。
            channel.setPtyType("xterm");
            channel.connect(1000);

            // 从shell创建出来这一刻开始，就可以和远程主机交互了
            sessionMap.put(session, new Shell(session, js, channel));
            return true;
        } catch (JSchException e) {
            String message = e.getMessage();
            if(message.equals("Auth fail")) {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,
                        "登录SSH失败，用户名或密码错误"));
                log.error("连接SSH失败，用户名或密码错误，登录失败");
            } else if(message.contains("Connection refused")) {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,
                        "连接被拒绝，可能是没有启动SSH服务或是放开端口"));
                log.error("连接SSH失败，连接被拒绝，可能是没有启动SSH服务或是放开端口");
            } else {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, message));
                log.error("连接SSH时出现错误", e);
            }
        }
        return false;
    }

    private class Shell {
        private final Session session;
        private final com.jcraft.jsch.Session js;
        private final ChannelShell channel;
        private final InputStream input;
        private final OutputStream output;

        public Shell(Session session, com.jcraft.jsch.Session js, ChannelShell channel) throws IOException {
            this.js = js;
            this.session = session;
            this.channel = channel;
            this.input = channel.getInputStream();
            this.output = channel.getOutputStream();
            service.submit(this::read);
        }

        // todo 0.1 远程主机到前端
        // 这个方法会在输入流结束或者发生异常就停下，但是为什么在此处这个方法不会停？
        // 答案：如果输入流是一个永不结束的源（例如，一个网络连接），那么 read 方法可能会一直执行。
        private void read() {
            try {
                byte[] buffer = new byte[1024 * 1024];
                int i;
                // 读取远程
                while ((i = input.read(buffer)) != -1) {
                    String text = new String(Arrays.copyOfRange(buffer, 0, i), StandardCharsets.UTF_8);
                    // 把读取的数据发送到控制台
                    session.getBasicRemote().sendText(text);
                }
            } catch (Exception e) {
                log.error("读取SSH输入流时出现问题", e);
            }
        }

        public void close() throws IOException {
            input.close();
            output.close();
            channel.disconnect();
            js.disconnect();
            service.shutdown();
        }
    }

    @Data
    private class Host {
        public String ip = "43.142.255.211";
        public int port = 22;
        public String username = "ubuntu";
        public String password = "Chendanfeng10";
    }
}
