package com.lwy.studynetty.danmu;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author 李卫勇
 * @date 2020-09-09 1:02
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //客户端连接池
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //读取消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                TextWebSocketFrame msg) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if(channel != incoming){
                channel.writeAndFlush(new TextWebSocketFrame(msg.text()));
            }else {
                channel.writeAndFlush(new TextWebSocketFrame("我发送的："+msg.text()));
            }
        }
    }

    //有客户端加入
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        Channel incoming = ctx.channel();
        channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + "加入"));

        channels.add(incoming);

        System.out.println("Client:"+ incoming.remoteAddress() +"加入");
    }

    //有客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incoming = ctx.channel();
        channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + "离开"));

        channels.remove(incoming);

        System.out.println("Client:"+ incoming.remoteAddress() +"离开");
    }


}
