package cn.hanabi.irc.management;

import cn.hanabi.irc.utils.DESUtil;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/11/27
 */
public class ClientData {

    //这里存储的是客户端的大部分字符串，如果登陆失败客户端将获得NULL
    public static String data = new String(DESUtil.encrypt((
            "client/icons/clickgui/logo.png;" +
                    "client/guis/clickgui/drag.png;" +
                    "client/icons/clickgui/;" +
                    ".png;" +
                    "client/icons/clickgui/disabled.png;" +
                    "client/icons/clickgui/enabled.png;" +
                    "shaders/post/blur.json;" +
                    "Radius;" +
                    "BlurDir;"
    ).getBytes(), DESUtil.initKey("ASD4ASD6ASD1561AS6DQWEQWEQAWEFWWEFH1A3N2A81B890I...")));


    public static String data2 = new String(DESUtil.encrypt((
            "XYZ:;" +
                    "Hurt:;" +
                    "Block:;" +
                    "Speed;" +
                    "WatchDog;" +
                    "Max:;"
    ).getBytes(), DESUtil.initKey("ASD4ASD6ASD1561AS6DQWEQWEQAWEFWWEFH1A3N2A81B890I...")));

}
