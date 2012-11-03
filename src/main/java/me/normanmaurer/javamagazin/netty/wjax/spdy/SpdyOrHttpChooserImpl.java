package me.normanmaurer.javamagazin.netty.wjax.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.spdy.SpdyOrHttpChooser;
import org.eclipse.jetty.npn.NextProtoNego;

import javax.net.ssl.SSLEngine;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class SpdyOrHttpChooserImpl extends SpdyOrHttpChooser {

    public SpdyOrHttpChooserImpl(int maxSpdyContentLength, int maxHttpContentLength) {
        super(maxSpdyContentLength, maxHttpContentLength);
    }

    @Override
    protected SelectedProtocol getProtocol(SSLEngine engine) {
        ProviderImpl provider = (ProviderImpl) NextProtoNego.get(engine);
        String protocol = provider.getSelectedProtocol();
        if (protocol == null) {
            return SelectedProtocol.None;
        }
        switch (protocol) {
            case "spdy/2":
                return SelectedProtocol.SpdyVersion2;
            case "spdy/3":
                return SelectedProtocol.SpdyVersion3;
            case "http/1.1":
                return SelectedProtocol.HttpVersion1_1;
            case "http/1.0":
                return SelectedProtocol.HttpVersion1_0;
            default:
                return SelectedProtocol.None;
        }
    }

    @Override
    protected ChannelInboundHandler createHttpRequestHandlerForHttp() {
        return new HttpRequestHandler();
    }

    @Override
    protected ChannelInboundHandler createHttpRequestHandlerForSpdy() {
        return new SpdyRequestHandler();
    }
}