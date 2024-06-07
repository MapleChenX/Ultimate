package personal.MapleChenX.imTcp.test.upload;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * in.readableBytes()
 * in.readInt()
 * in.markReaderIndex()
 * in.resetReaderIndex()
 */

// 协议 cmd(4) + length(4) + fileName + length(4) + fileContent

public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否至少有四个字节（一个int）可读
        if (in.readableBytes() < 8) {
            return;
        }

        // 目的：虽然人家发的时候是一个一个包发的，但是这些包在TCP层面上会组合为一个流，我们必须进行边界判断；
        // 而此处标记重置的目的是为了以防万一，因为一个巨大的包可能会被拆分成多个包变成in，而这是不完整的，我们需要等待更多的数据到来，所以我们需要重置读指针，等待更多的字节

        in.markReaderIndex();
        int cmd = in.readInt();
        int nameLen = in.readInt();
        FileDTO fileDTO = new FileDTO();
        fileDTO.setCmd(cmd);
        if (in.readableBytes() < nameLen) {
            in.resetReaderIndex();
            return;
        }
        byte[] fileName = new byte[nameLen];
        in.readBytes(fileName);
        fileDTO.setFileName(new String(fileName, StandardCharsets.UTF_8));

        if(cmd == 1){
            out.add(fileDTO);
            System.out.println(fileDTO);
        }else if(cmd ==2){
            int contentLen = in.readInt();
            byte[] fileContent = new byte[contentLen];
            in.readBytes(fileContent);
            fileDTO.setFileContent(fileContent);
            out.add(fileDTO);
        }
    }
}

