package personal.MapleChenX.imTcp.test.upload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 读空闲（服务器端）
                System.out.println("读空闲，关闭连接");
                ctx.close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                // 写空闲（客户端）
                System.out.println("写空闲，发送心跳");
                ctx.writeAndFlush("heartbeat");
            }
        }
    }
}
