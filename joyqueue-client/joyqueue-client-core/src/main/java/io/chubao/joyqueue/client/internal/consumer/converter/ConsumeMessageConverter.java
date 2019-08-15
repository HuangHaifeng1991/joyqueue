package io.chubao.joyqueue.client.internal.consumer.converter;

import com.google.common.collect.Lists;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeMessage;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeReply;
import io.chubao.joyqueue.network.command.RetryType;

import java.util.List;

/**
 * ConsumeMessageConverter
 *
 * author: gaohaoxiang
 * date: 2019/1/11
 */
public class ConsumeMessageConverter {

    public static List<ConsumeReply> convertToReply(List<ConsumeMessage> messages, RetryType retryType) {
        List<ConsumeReply> result = Lists.newArrayListWithCapacity(messages.size());
        for (ConsumeMessage message : messages) {
            result.add(new ConsumeReply(message.getPartition(), message.getIndex(), retryType));
        }
        return result;
    }
}