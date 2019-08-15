package io.chubao.joyqueue.network.protocol;

import io.netty.channel.ChannelHandler;

/**
 * CommandHandlerProvider
 *
 * author: gaohaoxiang
 * date: 2018/8/16
 */
public interface CommandHandlerProvider {

    ChannelHandler getCommandHandler(ChannelHandler channelHandler);
}