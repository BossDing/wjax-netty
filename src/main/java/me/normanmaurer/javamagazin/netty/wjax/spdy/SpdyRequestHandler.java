package me.normanmaurer.javamagazin.netty.wjax.spdy;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class SpdyRequestHandler extends HttpRequestHandler {

    @Override
    protected String getContent() {
        return "Serve via SPDY\r\n";
    }

}
