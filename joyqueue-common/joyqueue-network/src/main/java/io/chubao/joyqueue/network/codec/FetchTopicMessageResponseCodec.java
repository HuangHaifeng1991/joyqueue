package io.chubao.joyqueue.network.codec;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.chubao.joyqueue.exception.JoyQueueCode;
import io.chubao.joyqueue.message.BrokerMessage;
import io.chubao.joyqueue.network.command.FetchTopicMessageResponse;
import io.chubao.joyqueue.network.command.FetchTopicMessageAckData;
import io.chubao.joyqueue.network.command.JoyQueueCommandType;
import io.chubao.joyqueue.network.serializer.Serializer;
import io.chubao.joyqueue.network.transport.codec.JoyQueueHeader;
import io.chubao.joyqueue.network.transport.codec.PayloadCodec;
import io.chubao.joyqueue.network.transport.command.Type;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * FetchTopicMessageResponseCodec
 *
 * author: gaohaoxiang
 * date: 2018/12/7
 */
public class FetchTopicMessageResponseCodec implements PayloadCodec<JoyQueueHeader, FetchTopicMessageResponse>, Type {

    @Override
    public FetchTopicMessageResponse decode(JoyQueueHeader header, ByteBuf buffer) throws Exception {
        Map<String, FetchTopicMessageAckData> result = Maps.newHashMap();
        short topicSize = buffer.readShort();
        for (int i = 0; i < topicSize; i++) {
            String topic = Serializer.readString(buffer, Serializer.SHORT_SIZE);
            short messageSize = buffer.readShort();
            List<BrokerMessage> messages = Lists.newArrayListWithCapacity(messageSize);
            for (int j = 0; j < messageSize; j++) {
                messages.add(Serializer.readBrokerMessage(buffer));
            }
            JoyQueueCode code = JoyQueueCode.valueOf(buffer.readInt());
            FetchTopicMessageAckData fetchTopicMessageAckData = new FetchTopicMessageAckData(messages, code);
            result.put(topic, fetchTopicMessageAckData);
        }

        FetchTopicMessageResponse fetchTopicMessageResponse = new FetchTopicMessageResponse();
        fetchTopicMessageResponse.setData(result);
        return fetchTopicMessageResponse;
    }

    @Override
    public void encode(FetchTopicMessageResponse payload, ByteBuf buffer) throws Exception {
        buffer.writeShort(payload.getData().size());
        for (Map.Entry<String, FetchTopicMessageAckData> entry : payload.getData().entrySet()) {
            FetchTopicMessageAckData fetchTopicMessageAckData = entry.getValue();
            Serializer.write(entry.getKey(), buffer, Serializer.SHORT_SIZE);
            buffer.writeShort(fetchTopicMessageAckData.getBuffers().size());
            for (ByteBuffer rByteBuffer : fetchTopicMessageAckData.getBuffers()) {
                buffer.writeBytes(rByteBuffer);
            }
            buffer.writeInt(fetchTopicMessageAckData.getCode().getCode());
        }
    }

    @Override
    public int type() {
        return JoyQueueCommandType.FETCH_TOPIC_MESSAGE_RESPONSE.getCode();
    }
}
