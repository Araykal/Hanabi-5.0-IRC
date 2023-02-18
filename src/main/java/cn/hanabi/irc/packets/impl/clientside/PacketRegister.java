package cn.hanabi.irc.packets.impl.clientside;

import cn.hanabi.irc.packets.Packet;

public class PacketRegister extends Packet {
    public String username, password, key, hwid;

    public PacketRegister(String username, String password, String key, String hwid) {
        super(Type.REGISTER);
        this.username = username;
        this.password = password;
        this.key = key;
        this.hwid = hwid;
    }
}
