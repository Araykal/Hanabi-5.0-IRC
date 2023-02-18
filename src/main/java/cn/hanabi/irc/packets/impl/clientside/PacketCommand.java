package cn.hanabi.irc.packets.impl.clientside;

import cn.hanabi.irc.packets.Packet;

public class PacketCommand extends Packet {
    public String[] command;

    public PacketCommand(String[] command) {
        super(Packet.Type.COMMAND);
        this.command = command;
    }
}
