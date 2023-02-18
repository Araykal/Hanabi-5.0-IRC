package cn.hanabi.irc.server.database;

import cn.hanabi.irc.management.User;
import cn.hanabi.irc.server.LogUtil;
import cn.hanabi.irc.server.ServerMain;
import cn.hanabi.irc.server.utils.RandomUtils;
import cn.hanabi.irc.utils.DESUtil;
import cn.hanabi.irc.utils.MD5Utils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    static Connection connection;

    public static String address;
    public static String port;
    public static String name;
    public static String userName;
    public static String pwd;

    public static void init(String address, String port, String dbName, String user, String pwd) {
        DBHelper.address = address;
        DBHelper.port = port;
        DBHelper.name = dbName;
        DBHelper.userName = user;
        DBHelper.pwd = pwd;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("jdbc:mysql://" + address + ":" + port + "/" + dbName);
            connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + dbName + "?serverTimezone=UTC&autoReconnect=true&autoReconnectForPools=true", user, pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reconnect() {
        try {
            connection.close();
            connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + name + "?serverTimezone=UTC&autoReconnect=true&autoReconnectForPools=true", userName, pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //获取数据库中用户所有信息
    public static List<User> getUsers() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> keyList = new ArrayList<>();
            while (resultSet.next()) {
                keyList.add(new User(
                        //获取数据库中的username
                        resultSet.getString(1),
                        //获取数据库中的password
                        resultSet.getString(2),
                        //获取数据库中的hwid
                        resultSet.getString(4),
                        ServerMain.VERSION,
                        //获取数据库中的密钥
                        new String(DESUtil.decrypt(resultSet.getString(6)))));
            }
            return keyList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //获取数据库中的用户列表
    public static List<String> getUsersInfo() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> keyList = new ArrayList<>();
            while (resultSet.next()) {
                keyList.add("Username: " + resultSet.getString(1) + " Rank:" + resultSet.getString(3));
            }
            return keyList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //获取密钥列表
    public static List<String> getKeyList() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `activekeys`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> keyList = new ArrayList<>();
            while (resultSet.next()) {
                keyList.add("key: " + resultSet.getString(1) + " rank:" + resultSet.getString(2));
            }
            return keyList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param num 生成密钥数量
     */
    public static void addKey(int num, String rank) {
        for (int i = 0; i < num; i++) {
            try {
                String key = "Hanabi" + RandomUtils.getRandomString(16);
                //添加密钥并给密钥权限
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `activekeys` (`activekey`,`rank`) values (?,?)");
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, rank);
                preparedStatement.execute();
                preparedStatement.close();
                LogUtil.info("Add activekey " + key);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String login(String username, String password, String hwid, String activeKey) {
        reconnect();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE `username` = ? AND `password_md5` = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString(4).equals(hwid) && rs.getString(6).equals(activeKey)) {
                    return rs.getString(3);
                } else {
                    if (System.currentTimeMillis() - rs.getLong(5) >= 3600 * 24 * 7) {//判断HWID更新时间
                        PreparedStatement st = connection.prepareStatement("UPDATE `users` SET `hwid`=?,`updatetime`=? WHERE username=?");
                        st.setString(1, hwid);
                        st.setInt(2, (int) System.currentTimeMillis());
                        st.setString(3, username);
                        st.execute();
                        return rs.getString(3);
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        return "HWID is unverified, and you can't update your HWID until " + sdf.format(new Date(rs.getLong(5)));
                    }
                }
            }
            statement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Failed to login, Please check your username and password";
    }

    public static String register(String username, String password, String key, String hwid) {
        reconnect();
        if (username.isEmpty()) {
            return "Username cannot be empty;false";
        }
        if (password.isEmpty()) {
            return "Password cannot be empty;false";
        }
        if (hwid.isEmpty()) {
            return "Invalid machine codes;false";
        }
        password = MD5Utils.getMD5(password);
        try {
            // 检查用户是否已存在
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `username` = ?");
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return "User already exists;false";
            }
            // 检查key是否可用
            String rank = "WTF";
            if (!key.isEmpty()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `activekeys` WHERE `activekey` = ?");
                preparedStatement.setString(1, key);
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    rank = rs.getString(2);
                }
                if (rank.equals("WTF")) {
                    return "wrong key;false";
                }
            }

            //注册用户
            preparedStatement = connection.prepareStatement("INSERT INTO `users` (`username`, `password_md5`, `rank`, `hwid`, `updatetime` ,`activekey`) VALUES ( ? , ? , ?, ?, ?,?);");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, rank);
            preparedStatement.setString(4, hwid);
            preparedStatement.setLong(5, System.currentTimeMillis());
            preparedStatement.setString(6, key);
            boolean reg = preparedStatement.execute();

            //删除key
            if (!key.isEmpty()) {
                preparedStatement = connection.prepareStatement("DELETE FROM activekeys WHERE activekey = ?");
                preparedStatement.setString(1, key);
                if (preparedStatement.execute()) {
                    System.out.println("WTF Failed to remove key:" + key);
                }
            }

            preparedStatement.close();
            rs.close();
            if (!reg) {
                return "Register successfully;true";
            } else {
                return "failed to register;false";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown error;false";
    }


    public static void record(String username, String ip, String time) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO `ip_record` (`username`, `ip`, `time`) VALUES ( ? , ? , ?);");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, ip);
            preparedStatement.setString(3, time);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeUser(String user) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM `users` where `username`=?;");
            preparedStatement.setString(1, user);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
