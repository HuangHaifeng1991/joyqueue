package io.chubao.joyqueue.broker.kafka.network.codec;

import io.chubao.joyqueue.broker.kafka.coordinator.CoordinatorType;
import io.chubao.joyqueue.broker.kafka.network.KafkaPayloadCodec;
import io.chubao.joyqueue.broker.kafka.KafkaCommandType;
import io.chubao.joyqueue.broker.kafka.command.FindCoordinatorRequest;
import io.chubao.joyqueue.broker.kafka.command.FindCoordinatorResponse;
import io.chubao.joyqueue.broker.kafka.model.KafkaBroker;
import io.chubao.joyqueue.broker.kafka.network.KafkaHeader;
import io.chubao.joyqueue.network.serializer.Serializer;
import io.chubao.joyqueue.network.transport.command.Type;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;

/**
 * FindCoordinatorRequestCodec
 *
 * author: gaohaoxiang
 * date: 2018/11/5
 */
public class FindCoordinatorCodec implements KafkaPayloadCodec<FindCoordinatorResponse>, Type {

    @Override
    public FindCoordinatorRequest decode(KafkaHeader header, ByteBuf buffer) throws Exception {
        FindCoordinatorRequest request = new FindCoordinatorRequest();
        request.setCoordinatorKey(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        if (header.getVersion() >= 1) {
            request.setCoordinatorType(CoordinatorType.valueOf(buffer.readByte()));
        }
        return request;
    }

    @Override
    public void encode(FindCoordinatorResponse payload, ByteBuf buffer) throws Exception {
        short version = payload.getVersion();
        if (version >= 1) {
            // throttle_time_ms
            buffer.writeInt(payload.getThrottleTimeMs());
        }

        // 错误码
        buffer.writeShort(payload.getErrorCode());
        if (version >= 1) {
            Serializer.write(StringUtils.EMPTY, buffer, Serializer.SHORT_SIZE);
        }

        KafkaBroker broker = payload.getBroker();
        buffer.writeInt(broker.getId());
        Serializer.write(broker.getHost(), buffer, Serializer.SHORT_SIZE);
        buffer.writeInt(broker.getPort());
    }

    @Override
    public int type() {
        return KafkaCommandType.FIND_COORDINATOR.getCode();
    }
}