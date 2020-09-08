package com.lwy.studynetty.danmu;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.springframework.web.HttpRequestHandler;

/**
 * @author 李卫勇
 * @date 2020-09-09 0:53
 */
public class WebsocketDanmuServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-decode", new HttpRequestDecoder());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http-encode", new HttpResponseEncoder());


//        pipeline.addLast("http-request", new HttpRequestHandler("/ws"));

        //实现webSocket协议的编码与解码
        pipeline.addLast("WebSocket-protocol", new WebSocketServerProtocolHandler("/ws"));

        //实现弹幕发送业务
        pipeline.addLast("WebSocket-request", new TextWebSocketFrameHandler());


    }

}
