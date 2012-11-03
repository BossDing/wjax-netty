package me.normanmaurer.javamagazin.netty.wjax.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;


/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class HttpRequestHandler extends ChannelInboundMessageHandlerAdapter<HttpRequest> {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, HttpRequest request) throws Exception {

        if (HttpHeaders.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
        ByteBuf buf = Unpooled.copiedBuffer(getContent(), CharsetUtil.ISO_8859_1);
        response.setContent(buf);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        if (keepAlive) {
            response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, response.getContent().readableBytes());
            response.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ChannelFuture future = ctx.write(response);
        if (keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected String getContent() {
        return "Serve via HTTP\r\n";
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
