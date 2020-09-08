package com.lwy.studynetty.danmu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

/**
 * @author 李卫勇
 * @date 2020-09-08 23:35
 */
public class LubanHttpServer {

    public void openServer(int port) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        EventLoopGroup main = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup(8);
        bootstrap.group(main, work);

        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                //解码请求
                channel.pipeline().addLast("http-decode", new HttpRequestDecoder());
                //编码返回结果
                channel.pipeline().addLast("http-encode", new HttpResponseEncoder());

                //servlet 来处理业务请求
                channel.pipeline().addLast("http-servlet", new HttpServletHandler());
            }
        });

        //绑定端口号
        ChannelFuture f = bootstrap.bind(port).sync();
        System.out.println("服务已启动port:"+port);
        f.channel().closeFuture().sync();
    }



    public class HttpServletHandler extends SimpleChannelInboundHandler{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof LastHttpContent){
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
                response.content().writeBytes("lwy is goot a man".getBytes());

                //添加关闭监听器
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        new LubanHttpServer().openServer(8080);
    }
}
