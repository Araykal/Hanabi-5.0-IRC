package cn.hanabi.irc.packets.impl.serverside;

import cn.hanabi.irc.packets.Packet;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/11/27
 */
public class PacketData2Rep extends Packet {

    public String data;

    public PacketData2Rep(String content) {
        super(Type.REQUESTREP2, content);
        data = content;
    }
}