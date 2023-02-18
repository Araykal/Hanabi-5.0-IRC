package cn.hanabi.irc.server.utils;

import java.util.Random;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/11/26
 */
public class RandomUtils {

    public static String getRandomString(int length) {
        String str = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXZY";

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i % 4 == 0) {
                sb.append("-");
            }
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
