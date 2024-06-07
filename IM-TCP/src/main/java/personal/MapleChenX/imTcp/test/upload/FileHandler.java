package personal.MapleChenX.imTcp.test.upload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FileDTO fileDTO = (FileDTO) msg;
        if(fileDTO.getCmd() == 1){
            System.out.println("收到文件名：" + fileDTO.getFileName());
        }else if(fileDTO.getCmd() == 2){
            System.out.println("收到文件内容：" + new String(fileDTO.getFileContent()));
            Path path = Paths.get("a.md");
            Files.write(path, fileDTO.getFileContent());
        }
    }
}
