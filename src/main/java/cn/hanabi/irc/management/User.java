package cn.hanabi.irc.management;

public class User {
    public String username;
    public String password;
    public String hwid;
    public String text;

    public RankManager.Ranks rank;

    public String activeKey;

    public String rankInGame;
    public String ingame;

    public User(String username, String password, String hwid, String text, String activeKey) {
        this.username = username;
        this.password = password;
        this.hwid = hwid;
        this.text = text;
        this.activeKey = activeKey;
    }
}
