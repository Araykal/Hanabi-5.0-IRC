package cn.hanabi.irc.packets.impl;

import cn.hanabi.irc.packets.Packet;

public class PacketMessage extends Packet {

    public PacketMessage(String content) {
        super(Type.MESSAGE, content);
    }
}
