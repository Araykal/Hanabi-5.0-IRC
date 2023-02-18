package cn.hanabi.irc.packets.impl.serverside;

import cn.hanabi.irc.packets.Packet;

public class PacketRegisterRep extends Packet {

    private boolean success;
    private String key;

    public PacketRegisterRep(String result,boolean success,String key) {
        super(Type.REGISTERREP, result);
        this.success = success;
        this.key = key;
    }
}
