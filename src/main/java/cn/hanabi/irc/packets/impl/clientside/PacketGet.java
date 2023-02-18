package cn.hanabi.irc.packets.impl.clientside;

import cn.hanabi.irc.packets.Packet;

public class PacketGet extends Packet {
    public String id;

    public PacketGet(String id, String content) {
        super(Type.GET, content);
        this.id = id;
    }
}
