package io.chubao.joyqueue.broker.monitor.service.support;

import com.google.common.collect.Lists;
import io.chubao.joyqueue.broker.monitor.converter.BrokerMonitorConverter;
import io.chubao.joyqueue.broker.monitor.service.ConnectionMonitorService;
import io.chubao.joyqueue.broker.monitor.stat.AppStat;
import io.chubao.joyqueue.broker.monitor.stat.BrokerStat;
import io.chubao.joyqueue.broker.monitor.stat.ConnectionStat;
import io.chubao.joyqueue.broker.monitor.stat.TopicStat;
import io.chubao.joyqueue.monitor.Client;
import io.chubao.joyqueue.monitor.ConnectionMonitorDetailInfo;
import io.chubao.joyqueue.monitor.ConnectionMonitorInfo;

import java.util.List;
import java.util.Map;

/**
 * ConnectionMonitorService
 *
 * author: gaohaoxiang
 * date: 2018/10/15
 */
public class DefaultConnectionMonitorService implements ConnectionMonitorService {

    private BrokerStat brokerStat;

    public DefaultConnectionMonitorService(BrokerStat brokerStat) {
        this.brokerStat = brokerStat;
    }

    @Override
    public ConnectionMonitorInfo getConnectionInfo() {
        ConnectionStat connectionStat = brokerStat.getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorInfo(connectionStat);
    }

    @Override
    public ConnectionMonitorInfo getConnectionInfoByTopic(String topic) {
        TopicStat topicStat = brokerStat.getOrCreateTopicStat(topic);
        ConnectionStat connectionStat = topicStat.getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorInfo(connectionStat);
    }

    @Override
    public ConnectionMonitorInfo getConnectionInfoByTopicAndApp(String topic, String app) {
        TopicStat topicStat = brokerStat.getOrCreateTopicStat(topic);
        AppStat appStat = topicStat.getOrCreateAppStat(app);
        ConnectionStat connectionStat = appStat.getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorInfo(connectionStat);
    }

    @Override
    public ConnectionMonitorDetailInfo getConnectionDetailInfo() {
        ConnectionStat connectionStat = brokerStat.getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorDetailInfo(connectionStat);
    }

    @Override
    public ConnectionMonitorDetailInfo getConnectionDetailInfoByTopic(String topic) {
        TopicStat topicStat = brokerStat.getOrCreateTopicStat(topic);
        ConnectionStat connectionStat = topicStat.getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorDetailInfo(connectionStat);
    }

    @Override
    public ConnectionMonitorDetailInfo getConnectionDetailInfoByTopicAndApp(String topic, String app) {
        TopicStat topicStat = brokerStat.getOrCreateTopicStat(topic);
        AppStat appStat = topicStat.getOrCreateAppStat(app);
        ConnectionStat connectionStat = appStat.getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorDetailInfo(connectionStat);
    }

    @Override
    public ConnectionMonitorDetailInfo getConsumerConnectionDetailInfoByTopic(String topic) {
        ConnectionStat connectionStat = brokerStat.getOrCreateTopicStat(topic).getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorDetailInfo(connectionStat, filterConsumerClient(connectionStat.getConnectionMap()));
    }

    @Override
    public ConnectionMonitorDetailInfo getConsumerConnectionDetailInfoByTopicAndApp(String topic, String app) {
        ConnectionStat connectionStat = brokerStat.getOrCreateTopicStat(topic).getOrCreateAppStat(app).getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorDetailInfo(connectionStat, filterConsumerClient(connectionStat.getConnectionMap()));
    }

    @Override
    public ConnectionMonitorDetailInfo getProducerConnectionDetailInfoByTopic(String topic) {
        ConnectionStat connectionStat = brokerStat.getOrCreateTopicStat(topic).getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorDetailInfo(connectionStat, filterProducerClient(connectionStat.getConnectionMap()));
    }

    @Override
    public ConnectionMonitorDetailInfo getProducerConnectionDetailInfoByTopicAndApp(String topic, String app) {
        ConnectionStat connectionStat = brokerStat.getOrCreateTopicStat(topic).getOrCreateAppStat(app).getConnectionStat();
        return BrokerMonitorConverter.convertConnectionMonitorDetailInfo(connectionStat, filterProducerClient(connectionStat.getConnectionMap()));
    }

    protected List<Client> filterConsumerClient(Map<String, Client> clients) {
        List<Client> result = Lists.newLinkedList();
        for (Map.Entry<String, Client> entry : clients.entrySet()) {
            if (entry.getValue().isConsumerRole()) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    protected List<Client> filterProducerClient(Map<String, Client> clients) {
        List<Client> result = Lists.newLinkedList();
        for (Map.Entry<String, Client> entry : clients.entrySet()) {
            if (entry.getValue().isProducerRole()) {
                result.add(entry.getValue());
            }
        }
        return result;
    }
}