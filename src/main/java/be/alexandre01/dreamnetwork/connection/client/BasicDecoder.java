package be.alexandre01.dreamnetwork.connection.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class BasicDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < Character.BYTES) {
            return; // (3)
        }
        out.add(in.readBytes(in.readableBytes())); // (4)
    }
}
