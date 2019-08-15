package io.chubao.joyqueue.network.codec;

import io.chubao.joyqueue.network.command.JoyQueueCommandType;
import io.chubao.joyqueue.network.command.ProduceMessageRollbackRequest;
import io.chubao.joyqueue.network.serializer.Serializer;
import io.chubao.joyqueue.network.transport.codec.JoyQueueHeader;
import io.chubao.joyqueue.network.transport.codec.PayloadCodec;
import io.chubao.joyqueue.network.transport.command.Type;
import io.netty.buffer.ByteBuf;

/**
 * ProduceMessageRollbackRequestCodec
 *
 * author: gaohaoxiang
 * date: 2018/12/19
 */
public class ProduceMessageRollbackRequestCodec implements PayloadCodec<JoyQueueHeader, ProduceMessageRollbackRequest>, Type {

    @Override
    public ProduceMessageRollbackRequest decode(JoyQueueHeader header, ByteBuf buffer) throws Exception {
        ProduceMessageRollbackRequest produceMessageRollbackRequest = new ProduceMessageRollbackRequest();
        produceMessageRollbackRequest.setTopic(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        produceMessageRollbackRequest.setApp(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        produceMessageRollbackRequest.setTxId(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        return produceMessageRollbackRequest;
    }

    @Override
    public void encode(ProduceMessageRollbackRequest payload, ByteBuf buffer) throws Exception {
        Serializer.write(payload.getTopic(), buffer, Serializer.SHORT_SIZE);
        Serializer.write(payload.getApp(), buffer, Serializer.SHORT_SIZE);
        Serializer.write(payload.getTxId(), buffer, Serializer.SHORT_SIZE);
    }

    @Override
    public int type() {
        return JoyQueueCommandType.PRODUCE_MESSAGE_ROLLBACK_REQUEST.getCode();
    }
}