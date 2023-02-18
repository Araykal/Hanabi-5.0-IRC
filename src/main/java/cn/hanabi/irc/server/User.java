package cn.hanabi.irc.server;

import cn.hanabi.irc.management.RankManager;
import cn.hanabi.irc.server.database.DBHelper;
import cn.hanabi.irc.utils.MD5Utils;

public class User extends cn.hanabi.irc.management.User {

    public User(String username, String password, String hwid, String text, String activeKey) {
        super(username, password, hwid, text, activeKey);
    }

    public String login() {
        password = MD5Utils.getMD5(password);
        String res = DBHelper.login(username, password, hwid, activeKey);
        LogUtil.info(username + " tried to login:(" + res + ") Password:" + password + " HWID:" + hwid);
        switch (res) {
            case "Admin":
                rank = RankManager.Ranks.Admin;
                rankInGame = "§4[DEV]§r";
                break;
            case "Beta":
                rank = RankManager.Ranks.Beta;
                rankInGame = "§b[Beta]§r";
                break;
            case "Moderator":
                rank = RankManager.Ranks.Moderator;
                rankInGame = "§d[Moderator]§r";
                break;
            case "User":
                rank = RankManager.Ranks.User;
                rankInGame = "§7";
                break;
            case "Backer":
                rank = RankManager.Ranks.Backer;
                rankInGame = "§c[Backer]§r";
                break;
        }
        return res;
    }

}
