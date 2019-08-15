package io.chubao.joyqueue.tools;

import com.beust.jcommander.JCommander;
import io.chubao.joyqueue.tools.config.ConsoleConsumerConfig;
import io.openmessaging.KeyValue;
import io.openmessaging.MessagingAccessPoint;
import io.openmessaging.OMS;
import io.openmessaging.OMSBuiltinKeys;
import io.openmessaging.consumer.Consumer;
import io.openmessaging.consumer.MessageListener;
import io.openmessaging.extension.ExtensionHeader;
import io.openmessaging.joyqueue.domain.JoyQueueNameServerBuiltinKeys;
import io.openmessaging.message.Header;
import io.openmessaging.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * ConsoleConsumer
 *
 * author: gaohaoxiang
 * date: 2019/6/26
 */
public class ConsoleConsumer {

    protected static Logger logger = LoggerFactory.getLogger(ConsoleConsumer.class);

    public static void main(String[] args) {
        ConsoleConsumerConfig config = new ConsoleConsumerConfig();
        JCommander jcommander = JCommander.newBuilder()
                .addObject(config)
                .build();
        jcommander.parse(args);

        if (config.isHelp()) {
            jcommander.usage();
            return;
        }

        Consumer consumer = buildConsumer(config);
        consumer.start();

        run(consumer, config);
    }

    protected static Consumer buildConsumer(ConsoleConsumerConfig config) {
        KeyValue keyValue = OMS.newKeyValue();
        keyValue.put(OMSBuiltinKeys.ACCOUNT_KEY, config.getToken());
        keyValue.put(JoyQueueNameServerBuiltinKeys.NAMESPACE, StringUtils.defaultIfBlank(config.getNamespace(), ToolConsts.DEFAULT_NAMESPACE));

        for (Map.Entry<String, String> entry : config.getParams().entrySet()) {
            keyValue.put(entry.getKey(), entry.getValue());
        }

        MessagingAccessPoint messagingAccessPoint = OMS.getMessagingAccessPoint(
                String.format("oms:%s://%s@%s/%s",
                        ToolConsts.DRIVER, config.getApp(), config.getBootstrap(), StringUtils.defaultIfBlank(config.getRegion(), ToolConsts.DEFAULT_REGION)), keyValue);
        return messagingAccessPoint.createConsumer();
    }

    protected static void run(Consumer consumer, ConsoleConsumerConfig config) {
        consumer.bindQueue(config.getTopic(), new MessageListener() {
            @Override
            public void onReceived(Message message, Context context) {
                Header header = message.header();
                ExtensionHeader extensionHeader = message.extensionHeader().get();

                logger.info("Message{topic: {}, partition: {}, index: {}, txId: {}, key: {}, body: {}}",
                        header.getDestination(), extensionHeader.getPartiton(), extensionHeader.getOffset(),
                        extensionHeader.getTransactionId(), extensionHeader.getMessageKey(), new String(message.getData()));
            }
        });
    }
}