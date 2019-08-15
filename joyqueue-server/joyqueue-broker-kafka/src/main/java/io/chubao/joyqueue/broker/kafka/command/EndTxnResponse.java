package io.chubao.joyqueue.broker.kafka.command;

import io.chubao.joyqueue.broker.kafka.KafkaCommandType;

/**
 * EndTxnResponse
 *
 * author: gaohaoxiang
 * date: 2019/4/4
 */
public class EndTxnResponse extends KafkaRequestOrResponse {

    private short code;

    public EndTxnResponse() {

    }

    public EndTxnResponse(short code) {
        this.code = code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    @Override
    public int type() {
        return KafkaCommandType.END_TXN.getCode();
    }
}
