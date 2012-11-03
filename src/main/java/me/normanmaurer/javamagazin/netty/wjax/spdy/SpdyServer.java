package me.normanmaurer.javamagazin.netty.wjax.spdy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioEventLoop;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class SpdyServer {

    private final int port;

    public SpdyServer(int port) {
        this.port = port;
    }

    /**
     * Starten des Servers
     */
    public void startUp() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            Channel channel = bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(NioServerSocketChannel.class)
                    .childHandler(new SpdyChannelInitializer()).localAddress(new InetSocketAddress(port))
                    .bind().sync().channel();
            channel.closeFuture().sync();
        } finally {
            bootstrap.shutdown();

        }
    }

    public static void main(String args[]) throws Exception {
        int port;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8888;
        }
        new SpdyServer(port).startUp();
    }
}
