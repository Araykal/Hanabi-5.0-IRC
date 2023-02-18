package cn.hanabi.irc.command;

import cn.hanabi.irc.server.LogUtil;
import cn.hanabi.irc.server.database.DBHelper;
import cn.hanabi.irc.server.handler.NettyServerHandler;

import java.util.Scanner;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/11/26
 */
public class Command implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");

        while (true) {
            while (scanner.hasNextLine()) {
                String inputtedCommand = scanner.next();
                String[] command = inputtedCommand.split(",");
                String prefix = command[0];
                switch (prefix) {
                    case "help":
                        LogUtil.info("===============help===============");
                        LogUtil.info("add,num,rank        - Add Key.");
                        LogUtil.info("keylist        - Get Keys.");
                        LogUtil.info("del,name       - Delete User.");
                        LogUtil.info("help           - Show help.");
                        LogUtil.info("stop           - Stop Server.");
                        LogUtil.info("list           - Show User List.");
                        LogUtil.info("listol           - Show OnlineUser List.");
                        LogUtil.info("==================================");
                        break;
                    case "del":
                        if (command.length > 1) {
                            DBHelper.removeUser(command[1]);
                            LogUtil.info("Delete " + command[1]);
                        } else {
                            LogUtil.error("Invalid command: '" + inputtedCommand + "' Enter 'help' for help.");
                        }
                        break;
                    case "add":
                        if (command.length > 2) {
                            DBHelper.addKey(Integer.parseInt(command[1]), command[2]);
                        } else {
                            LogUtil.error("Invalid command: '" + inputtedCommand + "' Enter 'help' for help.");
                        }
                        break;
                    case "keylist":
                        DBHelper.getKeyList().forEach(LogUtil::info);
                        break;
                    case "stop":
                        LogUtil.info("Stopping...");
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.info("Stopped.");
                        System.exit(0);
                        break;
                    //获取数据库中的用户列表
                    case "list":
                        DBHelper.getUsersInfo().forEach(LogUtil::info);
                        break;
                    //获取在线用户列表
                    case "listol":
                        NettyServerHandler.users.forEach((channelHandlerContext, user) -> {
                            LogUtil.info(user.username);
                        });
                        break;
                    default:
                        LogUtil.error("Invalid command: '" + inputtedCommand + "' Enter 'help' for help.");
                        break;
                }
                System.out.print("> ");
            }
        }
    }
}
