package personal.MapleChenX.imTcp.test.upload;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashSet;
import java.util.Set;

public class MyHandler extends ChannelInboundHandlerAdapter {
    public static Set<Channel> clients = new HashSet<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        clients.forEach(e -> {
            e.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + "上线了");
        });
        clients.add(ctx.channel());
        System.out.println("[客户端]" + ctx.channel().remoteAddress() + "上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clients.forEach(e -> {
            e.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + "下线了");
        });
        System.out.println("[客户端]" + ctx.channel().remoteAddress() + "下线了");
        clients.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        System.out.println("收到数据：" + message);
        clients.forEach(e -> {
            if (e != ctx.channel()) {
                e.writeAndFlush("[客户端] " + ctx.channel().remoteAddress() + "：" + message);
            }
            else {
                e.writeAndFlush("[自己]：" + message);
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
