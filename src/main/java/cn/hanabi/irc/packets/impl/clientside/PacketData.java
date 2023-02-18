package cn.hanabi.irc.packets.impl.clientside;

import cn.hanabi.irc.packets.Packet;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/11/26
 */
public class PacketData extends Packet {
    public PacketData(String content) {
        super(Type.REQUEST, content);
    }
}
