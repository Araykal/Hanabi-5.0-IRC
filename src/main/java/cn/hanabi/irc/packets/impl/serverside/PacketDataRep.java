package cn.hanabi.irc.packets.impl.serverside;

import cn.hanabi.irc.packets.Packet;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/11/27
 */
public class PacketDataRep extends Packet {

    public String data;

    public PacketDataRep(String content) {
        super(Type.REQUESTREP, content);
        data = content;
    }
}
