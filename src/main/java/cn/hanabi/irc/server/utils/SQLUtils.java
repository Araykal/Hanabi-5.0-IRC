package cn.hanabi.irc.server.utils;

public class SQLUtils {
    public static boolean checkInject(String str) {
        str = str.toLowerCase();//统一转为小写
        String badStr = "select|update|and|or|delete|insert|truncate|char|into|substr|ascii|declare|exec|AckerRun|master|into|drop|execute|table";
        String[] badStrs = badStr.split("|");
        for (int i = 0; i < badStrs.length; i++) {
            if (str.indexOf(badStrs[i]) >= 0) {
                return true;
            }
        }
        return false;
    }
}
