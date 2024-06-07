package personal.MapleChenX.imTcp.test;

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
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否至少有四个字节（一个int）可读
        if (in.readableBytes() < 4) {
            return;
        }


        // 目的：虽然人家发的时候是一个一个包发的，但是这些包在TCP层面上会组合为一个流，我们必须进行边界判断；
        // 而此处标记重置的目的是为了以防万一，因为一个巨大的包可能会被拆分成多个包变成in，而这是不完整的，我们需要等待更多的数据到来，所以我们需要重置读指针，等待更多的字节

        in.markReaderIndex();
        // 从输入的ByteBuf中读取一个新的帧
        int length = in.readInt();
        if (in.readableBytes() < length) {
            // 如果可读字节不足，重置读指针，等待更多的字节
            in.resetReaderIndex();
            return;
        }

        // 读取指定长度的字节，并转换为字符串
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        String message = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("解码消息：" + message);
        // 添加到解码消息的列表中
        out.add(message);
    }
}

