package me.normanmaurer.netty.wjax.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class NettyEchoServer {

    public static void main(String[] args) throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(new OioEventLoopGroup(), new OioEventLoopGroup())
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(8888))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new EchoServerHandler());
                        }
                    });
            ChannelFuture f = b.bind().sync();

            f.channel().closeFuture().sync();
        } finally {
            b.shutdown();
        }
    }

    final static class EchoServerHandler extends ChannelInboundByteHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Accepted connection from " + ctx.channel());
        }

        @Override
        public void inboundBufferUpdated(ChannelHandlerContext ctx, ByteBuf in) {
            ByteBuf out = ctx.nextOutboundByteBuffer();
            out.discardReadBytes();
            out.writeBytes(in);
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
