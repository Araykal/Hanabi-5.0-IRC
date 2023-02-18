package cn.hanabi.irc.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {
    public static String getMD5(String text) {
        return DigestUtils.md5Hex(text);
    }
}
