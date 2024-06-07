package personal.MapleChenX.imTcp.test.upload;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class S {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, work)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new IdleStateHandler(3, 2, 5, TimeUnit.SECONDS));
//                    ch.pipeline().addLast(new HeartbeatHandler());
//                    ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                    ch.pipeline().addLast(new MyDecoder());
                    ch.pipeline().addLast(new FileHandler());
                }
            });
        serverBootstrap.bind(9000);

    }
}
