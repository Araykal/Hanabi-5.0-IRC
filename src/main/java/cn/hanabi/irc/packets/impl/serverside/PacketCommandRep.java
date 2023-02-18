package cn.hanabi.irc.packets.impl.serverside;

import cn.hanabi.irc.packets.Packet;

public class PacketCommandRep extends Packet {
    public String rep;

    public PacketCommandRep(String command) {
        super(Packet.Type.COMMAND_REP);
        this.rep = command;
    }
}
