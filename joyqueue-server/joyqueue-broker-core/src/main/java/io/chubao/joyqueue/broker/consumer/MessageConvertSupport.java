package io.chubao.joyqueue.broker.consumer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import io.chubao.joyqueue.broker.Plugins;
import io.chubao.joyqueue.message.BrokerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * MessageConvertSupport
 *
 * author: gaohaoxiang
 * date: 2019/4/3
 */
public class MessageConvertSupport {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Table<Byte /** type **/, Byte /** target **/, MessageConverter> converterTable;

    public MessageConvertSupport() {
        this.converterTable = loadConverters();
    }

    protected Table<Byte, Byte, MessageConverter> loadConverters() {
        Table<Byte, Byte, MessageConverter> result = HashBasedTable.create();
        Iterable<MessageConverter> iterable = Plugins.MESSAGE_CONVERTER.extensions();
        for (MessageConverter messageConverter : iterable) {
            result.put(messageConverter.type(), messageConverter.target(), messageConverter);
        }
        return result;
    }

    public List<BrokerMessage> convert(List<BrokerMessage> messages, byte target) {
        List<BrokerMessage> result = Lists.newLinkedList();
        for (BrokerMessage message : messages) {
            if (message.isBatch()) {
                List<BrokerMessage> convertedMessages = convertBatch(message, target);
                if (convertedMessages != null) {
                    result.addAll(convertedMessages);
                } else {
                    result.add(message);
                }
            } else {
                BrokerMessage convertedMessage = convert(message, target);
                if (convertedMessage != null) {
                    result.add(convertedMessage);
                } else {
                    result.add(message);
                }
            }
        }
        return result;
    }

    public BrokerMessage convert(BrokerMessage message, byte target) {
        MessageConverter messageConverter = converterTable.get(message.getSource(), target);
        if (messageConverter == null) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("convert message {} to {}, converter: {}", message.getSource(), target, messageConverter);
        }
        return messageConverter.convert(message);
    }

    public List<BrokerMessage> convertBatch(BrokerMessage message, byte target) {
        MessageConverter messageConverter = converterTable.get(message.getSource(), target);
        if (messageConverter == null) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("convert message {} to {}, converter: {}", message.getSource(), target, messageConverter);
        }
        return messageConverter.convertBatch(message);
    }
}