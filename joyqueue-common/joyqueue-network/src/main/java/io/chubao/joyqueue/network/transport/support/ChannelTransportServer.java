package io.chubao.joyqueue.network.transport.support;

import io.chubao.joyqueue.network.transport.TransportServerSupport;
import io.chubao.joyqueue.network.transport.config.ServerConfig;
import io.netty.channel.ChannelHandler;

/**
 * ChannelTransportServer
 *
 * author: gaohaoxiang
 * date: 2018/9/25
 */
public class ChannelTransportServer extends TransportServerSupport {

    private ChannelHandler channelHandler;

    public ChannelTransportServer(ChannelHandler channelHandler, ServerConfig serverConfig) {
        super(serverConfig);
        this.channelHandler = channelHandler;
    }

    public ChannelTransportServer(ChannelHandler channelHandler, ServerConfig serverConfig, String host) {
        super(serverConfig, host);
        this.channelHandler = channelHandler;
    }

    public ChannelTransportServer(ChannelHandler channelHandler, ServerConfig serverConfig, String host, int port) {
        super(serverConfig, host, port);
        this.channelHandler = channelHandler;
    }

    @Override
    protected ChannelHandler newChannelHandlerPipeline() {
        return this.channelHandler;
    }
}