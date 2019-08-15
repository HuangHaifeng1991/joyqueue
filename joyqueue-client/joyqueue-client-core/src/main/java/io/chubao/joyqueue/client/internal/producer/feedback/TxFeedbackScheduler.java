package io.chubao.joyqueue.client.internal.producer.feedback;

import io.chubao.joyqueue.client.internal.cluster.ClusterManager;
import io.chubao.joyqueue.client.internal.producer.MessageSender;
import io.chubao.joyqueue.client.internal.producer.callback.TxFeedbackCallback;
import io.chubao.joyqueue.client.internal.producer.feedback.config.TxFeedbackConfig;
import io.chubao.joyqueue.toolkit.concurrent.NamedThreadFactory;
import io.chubao.joyqueue.toolkit.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TxFeedbackScheduler
 *
 * author: gaohaoxiang
 * date: 2018/12/24
 */
public class TxFeedbackScheduler extends Service {

    protected static final Logger logger = LoggerFactory.getLogger(TxFeedbackScheduler.class);

    private TxFeedbackConfig config;
    private String topic;
    private TxFeedbackCallback txFeedbackCallback;
    private MessageSender messageSender;
    private ClusterManager clusterManager;
    private TxFeedbackDispatcher feedbackDispatcher;
    private ScheduledExecutorService scheduleThreadPool;

    public TxFeedbackScheduler(TxFeedbackConfig config, String topic, TxFeedbackCallback txFeedbackCallback, MessageSender messageSender, ClusterManager clusterManager) {
        this.config = config;
        this.topic = topic;
        this.txFeedbackCallback = txFeedbackCallback;
        this.messageSender = messageSender;
        this.clusterManager = clusterManager;
    }

    @Override
    protected void validate() throws Exception {
        feedbackDispatcher = new TxFeedbackDispatcher(config, topic, txFeedbackCallback, messageSender, clusterManager);
        scheduleThreadPool = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(String.format("joyqueue-txFeedback-scheduler-%s", topic), true));
    }

    @Override
    protected void doStart() throws Exception {
        scheduleThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                feedbackDispatcher.dispatch();
            }
        }, config.getFetchInterval(), config.getFetchInterval(), TimeUnit.MILLISECONDS);

//        logger.info("{} feedback is started", topic);
    }

    @Override
    protected void doStop() {
        if (scheduleThreadPool != null) {
            scheduleThreadPool.shutdown();
        }

        logger.info("{} feedbackp is stopped", topic);
    }
}