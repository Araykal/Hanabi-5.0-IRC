package cn.hanabi.irc.server;

import cn.hanabi.irc.packets.Packet;
import cn.hanabi.irc.server.handler.NettyServerHandler;

public class PacketServerRep extends cn.hanabi.irc.packets.impl.serverside.PacketServerRep {
    public PacketServerRep(String userRank, String serverVersion, String onlineUsersAckerRun, String content) {
        super(userRank, serverVersion, onlineUsersAckerRun, content);
        this.onlineUsersAckerRun = String.valueOf(NettyServerHandler.users.size());
    }
}
