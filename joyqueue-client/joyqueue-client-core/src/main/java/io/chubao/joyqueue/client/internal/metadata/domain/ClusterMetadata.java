package io.chubao.joyqueue.client.internal.metadata.domain;

import io.chubao.joyqueue.network.domain.BrokerNode;

import java.util.Map;

/**
 * ClusterMetadata
 *
 * author: gaohaoxiang
 * date: 2018/11/30
 */
public class ClusterMetadata {

    private Map<String, TopicMetadata> topics;
    private Map<Integer, BrokerNode> brokers;

    public ClusterMetadata(Map<String, TopicMetadata> topics, Map<Integer, BrokerNode> brokers) {
        this.topics = topics;
        this.brokers = brokers;
    }

    public TopicMetadata getTopic(String code) {
        return topics.get(code);
    }

    public BrokerNode getBrokerNode(int broker) {
        return brokers.get(broker);
    }

    public Map<String, TopicMetadata> getTopics() {
        return topics;
    }

    public Map<Integer, BrokerNode> getBrokers() {
        return brokers;
    }
}