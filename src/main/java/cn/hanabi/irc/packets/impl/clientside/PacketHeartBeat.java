package cn.hanabi.irc.packets.impl.clientside;

import cn.hanabi.irc.packets.Packet;

public class PacketHeartBeat extends Packet {
    public PacketHeartBeat(String content) {
        super(Type.HEARTBEAT, content);
    }
}
