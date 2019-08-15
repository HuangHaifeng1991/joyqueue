package io.chubao.joyqueue.client.internal.consumer.support;

import com.google.common.base.Preconditions;
import io.chubao.joyqueue.client.internal.cluster.ClusterManager;
import io.chubao.joyqueue.client.internal.consumer.MessagePoller;
import io.chubao.joyqueue.client.internal.consumer.config.ConsumerConfig;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeMessage;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeReply;
import io.chubao.joyqueue.client.internal.consumer.transport.ConsumerClientManager;
import io.chubao.joyqueue.client.internal.metadata.domain.TopicMetadata;
import io.chubao.joyqueue.client.internal.nameserver.NameServerConfig;
import io.chubao.joyqueue.exception.JoyQueueCode;
import io.chubao.joyqueue.toolkit.service.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * BroadcastMessagePoller
 *
 * author: gaohaoxiang
 * date: 2018/12/14
 */
public class BroadcastMessagePoller extends Service implements MessagePoller {

    private ConsumerConfig config;
    private NameServerConfig nameServerConfig;
    private ClusterManager clusterManager;
    private ConsumerClientManager consumerClientManager;
    private LocalConsumerIndexManager consumerIndexManager;
    private PartitionMessagePoller delegate;

    public BroadcastMessagePoller(ConsumerConfig config, NameServerConfig nameServerConfig, ClusterManager clusterManager, ConsumerClientManager consumerClientManager) {
        Preconditions.checkArgument(StringUtils.isNotBlank(config.getBroadcastGroup()), "consumer.broadcastGroup must be greater than 0");
        Preconditions.checkArgument(StringUtils.isNotBlank(config.getBroadcastLocalPath()), "consumer.broadcastLocalPath must not be null");

        this.config = config;
        this.nameServerConfig = nameServerConfig;
        this.clusterManager = clusterManager;
        this.consumerClientManager = consumerClientManager;
    }

    @Override
    protected void validate() throws Exception {
        consumerIndexManager = new LocalConsumerIndexManager(config, new DefaultConsumerIndexManager(clusterManager, consumerClientManager));
        delegate = new PartitionMessagePoller(config, nameServerConfig, clusterManager, consumerClientManager, consumerIndexManager);
    }

    @Override
    protected void doStart() throws Exception {
        consumerIndexManager.start();
        delegate.start();
    }

    @Override
    protected void doStop() {
        if (consumerIndexManager != null) {
            consumerIndexManager.stop();
        }
        delegate.stop();
    }

    @Override
    public ConsumeMessage pollOnce(String topic) {
        return delegate.pollOnce(topic);
    }

    @Override
    public ConsumeMessage pollOnce(String topic, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollOnce(topic, timeout, timeoutUnit);
    }

    @Override
    public List<ConsumeMessage> poll(String topic) {
        return delegate.poll(topic);
    }

    @Override
    public List<ConsumeMessage> poll(String topic, long timeout, TimeUnit timeoutUnit) {
        return delegate.poll(topic, timeout, timeoutUnit);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition) {
        return delegate.pollPartitionOnce(topic, partition);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionOnce(topic, partition, timeout, timeoutUnit);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition, long index) {
        return delegate.pollPartitionOnce(topic, partition, index);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition, long index, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionOnce(topic, partition, index, timeout, timeoutUnit);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition) {
        return delegate.pollPartition(topic, partition);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartition(topic, partition, timeout, timeoutUnit);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition, long index) {
        return delegate.pollPartition(topic, partition, index);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition, long index, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartition(topic, partition, index, timeout, timeoutUnit);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition) {
        return delegate.pollPartitionAsync(topic, partition);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionAsync(topic, partition, timeout, timeoutUnit);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition, long index) {
        return delegate.pollPartitionAsync(topic, partition, index);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition, long index, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionAsync(topic, partition, index, timeout, timeoutUnit);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollAsync(String topic) {
        return delegate.pollAsync(topic);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollAsync(String topic, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollAsync(topic, timeout, timeoutUnit);
    }

    @Override
    public JoyQueueCode reply(String topic, List<ConsumeReply> replyList) {
        return delegate.reply(topic, replyList);
    }

    @Override
    public JoyQueueCode replyOnce(String topic, ConsumeReply reply) {
        return delegate.replyOnce(topic, reply);
    }

    @Override
    public TopicMetadata getTopicMetadata(String topic) {
        return delegate.getTopicMetadata(topic);
    }
}