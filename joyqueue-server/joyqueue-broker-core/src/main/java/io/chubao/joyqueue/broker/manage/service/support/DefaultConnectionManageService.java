package io.chubao.joyqueue.broker.manage.service.support;

import com.google.common.collect.Lists;
import io.chubao.joyqueue.broker.manage.service.ConnectionManageService;
import io.chubao.joyqueue.network.session.Consumer;
import io.chubao.joyqueue.network.session.Producer;
import io.chubao.joyqueue.broker.monitor.SessionManager;

import java.util.List;

/**
 * ConnectionManageService
 *
 * author: gaohaoxiang
 * date: 2018/10/18
 */
public class DefaultConnectionManageService implements ConnectionManageService {

    private SessionManager sessionManager;

    public DefaultConnectionManageService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public int closeProducer(String topic, String app) {
        List<String> connectionIdList = Lists.newLinkedList();
        for (Producer producer : sessionManager.getProducer()) {
            if (producer.getTopic().equals(topic) && producer.getApp().equals(app)) {
                connectionIdList.add(producer.getConnectionId());
            }
        }
        for (String connectionId : connectionIdList) {
            sessionManager.getConnectionById(connectionId).getTransport().stop();
        }
        return connectionIdList.size();
    }

    @Override
    public int closeConsumer(String topic, String app) {
        List<String> connectionIdList = Lists.newLinkedList();
        for (Consumer consumer : sessionManager.getConsumer()) {
            if (consumer.getTopic().equals(topic) && consumer.getApp().equals(app)) {
                connectionIdList.add(consumer.getConnectionId());
            }
        }
        for (String connectionId : connectionIdList) {
            sessionManager.getConnectionById(connectionId).getTransport().stop();
        }
        return connectionIdList.size();
    }
}
