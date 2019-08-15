package io.chubao.joyqueue.broker.producer.transaction.command;

import io.chubao.joyqueue.network.command.CommandType;
import io.chubao.joyqueue.network.transport.command.JoyQueuePayload;

import java.util.List;

/**
 * TransactionRollbackRequest
 *
 * author: gaohaoxiang
 * date: 2019/4/12
 */
public class TransactionRollbackRequest extends JoyQueuePayload {

    private String topic;
    private String app;
    private List<String> txIds;

    public TransactionRollbackRequest() {

    }

    public TransactionRollbackRequest(String topic, String app, List<String> txIds) {
        this.topic = topic;
        this.app = app;
        this.txIds = txIds;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setTxIds(List<String> txIds) {
        this.txIds = txIds;
    }

    public List<String> getTxIds() {
        return txIds;
    }

    @Override
    public int type() {
        return CommandType.TRANSACTION_ROLLBACK_REQUEST;
    }

    @Override
    public String toString() {
        return "TransactionRollbackRequest{" +
                "topic='" + topic + '\'' +
                ", app='" + app + '\'' +
                ", txIds=" + txIds +
                '}';
    }
}