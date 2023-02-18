package cn.hanabi.irc.server.handler;

import cn.hanabi.irc.management.ClientData;
import cn.hanabi.irc.packets.impl.PacketMessage;
import cn.hanabi.irc.packets.impl.clientside.*;
import cn.hanabi.irc.packets.impl.serverside.*;
import cn.hanabi.irc.server.LogUtil;
import cn.hanabi.irc.utils.DESUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import cn.hanabi.irc.management.RankManager;
import cn.hanabi.irc.management.User;
import cn.hanabi.irc.packets.Packet;
import cn.hanabi.irc.server.database.DBHelper;
import cn.hanabi.irc.utils.PacketUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import static cn.hanabi.irc.utils.PacketUtil.unpack;


class Entry {
    public ChannelHandlerContext ctx;
    public Integer times;
    private long timestamp;

    public Entry(ChannelHandlerContext ctx, Integer times) {
        this.ctx = ctx;
        this.times = times;
        timestamp = System.currentTimeMillis();
    }

    public void update() {
        if (System.currentTimeMillis() - timestamp > 1000) {
            times = 0;
            timestamp = System.currentTimeMillis();
        }
    }
}

public class PacketHandler {
    static ArrayList<Entry> map = new ArrayList<Entry>();
    static ArrayList<String> blacklist = new ArrayList<String>();

    public static void handle(ChannelHandlerContext ctx, String o) {

        for (String s : blacklist) {
            LogUtil.warning("Blacklisted tried to connect " + ctx.channel().remoteAddress().toString());
            ctx.channel().writeAndFlush(PacketUtil.pack(new PacketMessage("You are already blacklisted.")));
            if (ctx.channel().remoteAddress().toString().contains(s)) {
                ctx.close();
                return;
            }
        }
        boolean has = false;
        for (Entry entry : map) {
            entry.update();
            if (entry.ctx == ctx) {
                has = true;
                entry.times++;
                if (entry.times > 30) {
                    blacklist.add(ctx.channel().remoteAddress().toString().split(":")[0]);
                    ctx.channel().writeAndFlush(PacketUtil.pack(new PacketMessage("\247cYou are blacklisted now because of spam.")));
                    ctx.channel().writeAndFlush(PacketUtil.pack(new PacketMessage("KICKUSER")));
                    LogUtil.warning("Blacklisted: " + ctx.channel().remoteAddress().toString());
                    ctx.close();
                    return;
                }
            }
        }
        if (!has)
            map.add(new Entry(ctx, 1));


        Channel channel = ctx.channel();
        if (o.contains("\247") || o.length() > 300)
            return;
        Packet p = unpack(o, Packet.class);
        switch (p.type) {
            case REQUEST:
                break;
            case LOGIN:
                try {
                    PacketLogin packetLogin = PacketUtil.unpack(o, PacketLogin.class);
                    LogUtil.log("User " + packetLogin.user.username + " " + packetLogin.user.password);
                    cn.hanabi.irc.server.User user = new cn.hanabi.irc.server.User(packetLogin.user.username, packetLogin.user.password, packetLogin.user.hwid, packetLogin.user.text, new String(DESUtil.decrypt(packetLogin.user.activeKey)));
                    //这一段留着被发现了会很容易被破解
                    /*if (user.text != null && user.text.equals("Skidline")) {
                        user.rankInGame = "\247r[Skidline]\2477";
                        NettyServerHandler.channelGroup.add(channel);
                        NettyServerHandler.users.put(ctx, user);
                        channel.writeAndFlush(PacketUtil.pack(new cn.hanabi.irc.server.PacketServerRep("Skidline", "1.3", String.valueOf(NettyServerHandler.users.size()), "skidline")));
                        NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]" + user.rankInGame + user.username + "§r connected to the irc.")));
                        LogUtil.info("Login successfully: " + user.rank + " " + user.username + " ip:" + ctx.channel().remoteAddress());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        DBHelper.record(user.username, ctx.channel().remoteAddress().toString(), sdf.format(System.currentTimeMillis()));
                    } else*/
                    {
                        LogUtil.info("Login from: " + " ip:" + ctx.channel().remoteAddress());
                        String rank = user.login();
                        LogUtil.info("Login from: " + user.username + user.rank);
                        if (user.rank == null) {
                            LogUtil.info("Login failed: " + user.username + " ip:" + ctx.channel().remoteAddress());
                            channel.close();
                            ctx.close();
                            NettyServerHandler.channelGroup.remove(ctx);
                            return;
                        } else {
                            NettyServerHandler.channelGroup.add(channel);
                            NettyServerHandler.users.put(ctx, user);
                            channel.writeAndFlush(PacketUtil.pack(new cn.hanabi.irc.server.PacketServerRep(rank, "1.3", String.valueOf(NettyServerHandler.users.size()), rank)));
                            NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]" + user.rankInGame + user.username + "§r connected to the irc.")));
                            LogUtil.info("Login successfully: " + user.rank + " " + user.username + " ip:" + ctx.channel().remoteAddress());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            DBHelper.record(user.username, ctx.channel().remoteAddress().toString(), sdf.format(System.currentTimeMillis()));
                            //写两个包是为了沾包，一个包发不完
                            ctx.channel().writeAndFlush(PacketUtil.pack(new PacketDataRep(ClientData.data)));
                            ctx.channel().writeAndFlush(PacketUtil.pack(new PacketData2Rep(ClientData.data2)));
                        }
                    }
                    NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC] Welcome to the irc, " + packetLogin.user.rankInGame + packetLogin.user.username + "§r!")));
                    NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]§r There are §4" + NettyServerHandler.users.size() + "§r users online.")));

                    for (User u : NettyServerHandler.users.values()) {
                        NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("\2477 - " + u.rankInGame + u.username)));
                    }
                    if (NettyServerHandler.users.size() > 15) {
                        NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§7And other " + (NettyServerHandler.users.size() - 15) + " users.")));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case COMMAND:
                PacketCommand packetCommand = PacketUtil.unpack(o, PacketCommand.class);
                if (NettyServerHandler.users.get(ctx).rank.equals(RankManager.Ranks.Admin)) {
                    if (packetCommand.command[0].equals("kick")) {
                        NettyServerHandler.users.forEach((key, value) -> {
                            if (value.username.equals(packetCommand.command[1])) {
                                key.channel().writeAndFlush(PacketUtil.pack(new PacketMessage("KICKUSER")));
                                NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]" + NettyServerHandler.users.get(ctx).username + "Kicked" + packetCommand.command[1])));
                                key.close();
                            }
                        });
                    }
                }
                break;
            case EXIT:
                ctx.close();
                break;
            case MESSAGE:
                PacketMessage packetMessage = PacketUtil.unpack(o, PacketMessage.class);
                User user1 = NettyServerHandler.users.get(ctx);
                if (NettyServerHandler.users.get(ctx) != null && NettyServerHandler.users.get(ctx).text.equals("Skidline")) {
                    NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]" + user1.rankInGame + user1.username + ": " + packetMessage.content)));
                } else {
                    NettyServerHandler.channelGroup.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]" + user1.rankInGame + user1.username + "[\2477" + user1.text + "\247r]" + ": " + packetMessage.content)));
                }
                break;
            case HEARTBEAT:
                PacketHeartBeat packetHeartBeat = PacketUtil.unpack(o, PacketHeartBeat.class);
                User user = NettyServerHandler.users.get(ctx);
                if (NettyServerHandler.users.get(ctx) != null) {
                    if ((packetHeartBeat.content).contains(user.username + ";" + user.password + ";" + user.hwid + ";" + DESUtil.encrypt(user.activeKey.getBytes()))) {
                        sendPacket(ctx.channel(), new PacketHeartBeatRep(String.valueOf(System.currentTimeMillis())));
                    } else {
                        sendPacket(ctx.channel(), new PacketDataRep(null));
                    }
/*                    if (NettyServerHandler.users.get(ctx).text.equals("Skidline")) {
                        NettyServerHandler.users.get(ctx).username = packetHeartBeat.content;
                    } else {
                        NettyServerHandler.users.get(ctx).text = packetHeartBeat.content;
                    }*/
                }
                break;
            case GET:
                PacketGet packetGet = PacketUtil.unpack(o, PacketGet.class);
                if (packetGet.content.equals("OnlineAckerRun")) {
                    ctx.writeAndFlush(PacketUtil.pack(new PacketGetRep(packetGet.id, "OnlineUsers:" + NettyServerHandler.users.size())));
                } else if (packetGet.content.equals("OnlineList")) {
                    StringBuilder sb = new StringBuilder();
                    for (User u : NettyServerHandler.users.values()) {
                        ctx.writeAndFlush(PacketUtil.pack(new PacketMessage(" \2474Hanabi User List")));
                        ctx.writeAndFlush(PacketUtil.pack(new PacketMessage(" - " + u.rankInGame + u.username)));
                    }
                } else if (packetGet.content.equals("help")) {
                    ctx.writeAndFlush(PacketUtil.pack(new PacketMessage(" \2474Hanabi IRC Help")));
                    ctx.writeAndFlush(PacketUtil.pack(new PacketMessage(" - OnlineList: Get online list")));
                    ctx.writeAndFlush(PacketUtil.pack(new PacketMessage(" - OnlineAckerRun: Get online AckerRun")));
                } else {
                    ctx.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]§r Unknown command.")));
                    ctx.writeAndFlush(PacketUtil.pack(new PacketMessage("§6[IRC]§r Use .irc get help to get help.")));
                }
                break;
            case REGISTER:
                PacketRegister packetRegister = PacketUtil.unpack(o, PacketRegister.class);
                String result = DBHelper.register(packetRegister.username, packetRegister.password, packetRegister.key, packetRegister.hwid);
                //System.out.println(result);
                String[] split = result.split(";");
                sendPacket(ctx.channel(), new PacketRegisterRep(split[0], Boolean.parseBoolean(split[1]), packetRegister.key));
                break;
        }
    }


    public static void sendPacket(RankManager.Ranks who, Packet packet) {
        for (Map.Entry<ChannelHandlerContext, User> e : NettyServerHandler.users.entrySet()) {
            if (check(e.getValue().rank, who)) {
                sendPacket(e.getKey().channel(), packet);
            }
        }
    }

    private static boolean check(RankManager.Ranks rank, RankManager.Ranks who) {
        if (who.equals(RankManager.Ranks.User))
            return true;
        if (rank.equals(who))
            return true;
        return false;
    }


    public static void sendPacket(Channel channel, Packet packet) {
        channel.writeAndFlush(PacketUtil.pack(packet));
    }
}
