package cn.hanabi.irc.utils;

import cn.hanabi.irc.server.LogUtil;
import com.google.gson.Gson;
import cn.hanabi.irc.packets.Packet;

public class PacketUtil {

    public static <T extends Packet> T unpack(String content, Class<T> type) {
        Gson gson = new Gson();
        T result = null;
        try {
            result = gson.fromJson(new String(DESUtil.decrypt(content)), type);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Json transform failed:" + content);
        }
        return result;
    }

    public static String pack(Packet packet) {
        Gson gson = new Gson();
        try {
            return DESUtil.encrypt(gson.toJson(packet).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
