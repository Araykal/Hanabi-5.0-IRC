package cn.hanabi.irc.server;

import cn.hanabi.irc.command.Command;
import cn.hanabi.irc.handler.DelimiterEncoder;
import cn.hanabi.irc.server.handler.NettyServerHandler;
import cn.hanabi.irc.server.database.DBHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ServerMain {
    public static ChannelFuture cf;
    static int port = 5557;
    public static String dbAddress, dbPort, dbName, dbUserName, dbPWD;
    public static boolean debug = false;
    public static final String VERSION = "5.0";


    public static void main(String[] args) {
        System.out.println("  _____   _____     _____ \n" +
                " |_   _| |  __ \\   / ____|\n" +
                "   | |   | |__) | | |     \n" +
                "   | |   |  _  /  | |     \n" +
                "  _| |_  | | \\ \\  | |____ \n" +
                " |_____| |_|  \\_\\  \\_____|");
        try {
/*            if (!args[0].isEmpty()) {
                port = Integer.valueOf(args[0]);
            }
            if (args[1].equals("on")) {
                debug = true;
            }*/
//            dbAddress = args[2];
            dbAddress = "127.0.0.1";
            dbPort = "3306";
//            dbPort = args[3];
//            dbName = args[4];
            dbName = "hanabi";
//            dbUserName = args[5];
            dbUserName = "root";
//            dbPWD = args[6];
            dbPWD = "redtea123";
        } catch (Exception e) {
            System.out.println("Error! Arguments: [port] [debug(on/off)] [dbAddress] [dbPort] [dbName] [dbUserName] [dbPassword]");
            System.exit(0);
        }

        DBHelper.init(dbAddress, dbPort, dbName, dbUserName, dbPWD);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            String delimiter = "_$_";
            bootstrap.group(group).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,
                                    Unpooled.wrappedBuffer(delimiter.getBytes())));
//                            socketChannel.pipeline().addLast(new DESDecoder());
                            socketChannel.pipeline().addLast("decoder", new StringDecoder());
                            socketChannel.pipeline().addLast("encoder", new StringEncoder());
                            socketChannel.pipeline().addLast(new DelimiterEncoder(delimiter));
//                            socketChannel.pipeline().addLast(new DESEncoder());
                            socketChannel.pipeline().addLast(new IdleStateHandler(10, 10, 10, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            try {
                cf = bootstrap.bind(port).sync();
                LogUtil.info("Server started!");
                new Thread(new Command()).start();
                cf.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            LogUtil.info("Server closed!");
            group.shutdownGracefully();
        }
    }
}
