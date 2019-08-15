package io.chubao.joyqueue.broker.kafka.coordinator.transaction.completion;

import io.chubao.joyqueue.broker.kafka.config.KafkaConfig;
import io.chubao.joyqueue.toolkit.concurrent.NamedThreadFactory;
import io.chubao.joyqueue.toolkit.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TransactionCompletionScheduler
 *
 * author: gaohaoxiang
 * date: 2019/4/15
 */
public class TransactionCompletionScheduler extends Service {

    protected static final Logger logger = LoggerFactory.getLogger(TransactionCompletionScheduler.class);

    private KafkaConfig config;
    private TransactionCompletionHandler transactionCompletionHandler;

    private ScheduledExecutorService executor;

    public TransactionCompletionScheduler(KafkaConfig config, TransactionCompletionHandler transactionCompletionHandler) {
        this.config = config;
        this.transactionCompletionHandler = transactionCompletionHandler;
    }

    @Override
    protected void validate() throws Exception {
        executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("joyqueue-transaction-compensate"));
    }

    @Override
    protected void doStart() throws Exception {
        executor.scheduleAtFixedRate(new TransactionCompletionThread(transactionCompletionHandler),
                config.getTransactionLogInterval(), config.getTransactionLogInterval(), TimeUnit.MILLISECONDS);
    }

    @Override
    protected void doStop() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
