package cn.hanabi.irc.packets.impl.serverside;

import cn.hanabi.irc.packets.Packet;

public class PacketHeartBeatRep extends Packet {
    public PacketHeartBeatRep( String content) {
        super(Type.HEARTBEATREP, content);
    }
}
