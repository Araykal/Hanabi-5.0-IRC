package cn.hanabi.irc.handler;

import cn.hanabi.irc.utils.DESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class DESDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte [] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        byte [] decrypt = DESUtil.decrypt(new String(bytes));
        ByteBuf buf = Unpooled.wrappedBuffer(decrypt);
        list.add(buf);
    }
}
