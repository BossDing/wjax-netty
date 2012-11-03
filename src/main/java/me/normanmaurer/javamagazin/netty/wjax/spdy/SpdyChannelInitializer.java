package me.normanmaurer.javamagazin.netty.wjax.spdy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.example.securechat.SecureChatSslContextFactory;
import io.netty.handler.ssl.SslHandler;
import org.eclipse.jetty.npn.NextProtoNego;

import javax.net.ssl.SSLEngine;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class SpdyChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);

        NextProtoNego.put(engine, new ProviderImpl());
        NextProtoNego.debug = true;

        pipeline.addLast("sslHandler", new SslHandler(engine));
        pipeline.addLast("chooser", new SpdyOrHttpChooserImpl(1024 * 1024, 1024 * 1024));
    }
}
