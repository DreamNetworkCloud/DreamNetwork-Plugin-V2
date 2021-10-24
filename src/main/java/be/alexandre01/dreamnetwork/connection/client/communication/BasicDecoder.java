package be.alexandre01.dreamnetwork.connection.client.communication;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class BasicDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("Decode");
        while (in.readableBytes() > 0) {
            in.markReaderIndex();
            if (in.readableBytes() < 4) return;
            int length = in.readInt();
            if (in.readableBytes() < length) { // Not all bytes received yet
                in.resetReaderIndex();
                return;
            }
            out.add(in.copy(in.readerIndex(), length));
            in.skipBytes(length);
        }
    }
}
