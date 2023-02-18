package cn.hanabi.irc.server.handler;

import cn.hanabi.irc.packets.impl.PacketMessage;
import cn.hanabi.irc.server.LogUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import cn.hanabi.irc.management.User;
import cn.hanabi.irc.packets.Packet;
import cn.hanabi.irc.utils.PacketUtil;

import java.text.SimpleDateFormat;
import java.util.*;

import static cn.hanabi.irc.utils.PacketUtil.unpack;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static HashMap<ChannelHandlerContext, User> users = new HashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent ent = (IdleStateEvent) evt;
            switch (ent.state()) {
                case ALL_IDLE:
                    LogUtil.info("User" + ctx.channel().remoteAddress() + " Disconnected because of heart disease");
                    ctx.close();
                    break;
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String o) throws Exception {
        Packet p = unpack(o, Packet.class);
        //输出发的包
//        System.out.println(p.type + "   " + p.content);
        if (p != null) {
            PacketHandler.handle(ctx, o);
        } else {
            System.out.println("[Packet Exception]" + ctx.channel().remoteAddress() + "    " + o);
            ctx.close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (users.get(ctx) != null) {
            channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]" + users.get(ctx).rankInGame + users.get(ctx).username + "§r exit IRC.")));
            LogUtil.info(users.get(ctx).username + " exited - " + ctx.channel().remoteAddress());
            users.remove(ctx);
            channelGroup.remove(channel);
        }
    }

    public static Object getKey(Map map, Object value) {
        List<Object> keyList = new ArrayList<>();
        for (Object key : map.keySet()) {
            if (map.get(key).equals(value)) {
                keyList.add(key);
            }
        }
        return keyList;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
