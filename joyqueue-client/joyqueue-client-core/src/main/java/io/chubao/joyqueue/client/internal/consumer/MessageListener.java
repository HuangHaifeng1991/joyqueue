package io.chubao.joyqueue.client.internal.consumer;

import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeMessage;

/**
 * MessageListener
 *
 * author: gaohaoxiang
 * date: 2018/12/25
 */
public interface MessageListener extends BaseMessageListener {

    void onMessage(ConsumeMessage message);
}