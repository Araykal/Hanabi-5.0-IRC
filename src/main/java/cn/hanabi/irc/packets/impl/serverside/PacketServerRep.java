package cn.hanabi.irc.packets.impl.serverside;

import cn.hanabi.irc.packets.Packet;

public class PacketServerRep extends Packet {
    public String userRank;
    public String serverVersion;
    public String onlineUsersAckerRun;

    public PacketServerRep(String userRank, String serverVersion, String onlineUsersAckerRun, String content) {
        super(Type.LOGINREP, content);
        this.userRank = userRank;
        this.serverVersion = serverVersion;
    }
}
