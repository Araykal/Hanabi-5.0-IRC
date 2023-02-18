package cn.hanabi.irc.packets.impl.clientside;

import cn.hanabi.irc.management.User;
import cn.hanabi.irc.packets.Packet;

public class PacketLogin extends Packet {
    public User user;
    public String version;


    public PacketLogin(String username, String password, String hwid, String text,String activeKey) {
        super(Packet.Type.LOGIN);
        user = new User(username, password, hwid, text, activeKey);
    }
}
