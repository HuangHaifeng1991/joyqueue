package io.chubao.joyqueue.broker.kafka.network.codec;

import com.google.common.collect.Lists;
import io.chubao.joyqueue.broker.kafka.KafkaCommandType;
import io.chubao.joyqueue.broker.kafka.KafkaErrorCode;
import io.chubao.joyqueue.broker.kafka.command.TopicMetadataRequest;
import io.chubao.joyqueue.broker.kafka.command.TopicMetadataResponse;
import io.chubao.joyqueue.broker.kafka.model.KafkaBroker;
import io.chubao.joyqueue.broker.kafka.model.KafkaPartitionMetadata;
import io.chubao.joyqueue.broker.kafka.model.KafkaTopicMetadata;
import io.chubao.joyqueue.broker.kafka.network.KafkaHeader;
import io.chubao.joyqueue.broker.kafka.network.KafkaPayloadCodec;
import io.chubao.joyqueue.network.serializer.Serializer;
import io.chubao.joyqueue.network.transport.command.Type;
import io.chubao.joyqueue.network.transport.exception.TransportException;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TopicMetadataCodec
 *
 * author: gaohaoxiang
 * date: 2018/11/5
 */
public class TopicMetadataCodec implements KafkaPayloadCodec<TopicMetadataResponse>, Type {

    @Override
    public Object decode(KafkaHeader header, ByteBuf buffer) throws Exception {
        TopicMetadataRequest topicMetadataRequest = new TopicMetadataRequest();
        int numTopics = buffer.readInt();
//        if (numTopics < 0) {
//            throw new KafkaException("number of topics has value " + numTopics + " which is invalid");
//        }

        List<String> topics = Lists.newLinkedList();
        for (int i = 0; i < numTopics; i++) {
            topics.add(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        }
        topicMetadataRequest.setTopics(topics);
        if (header.getVersion() >= 4) {
            // allow_auto_topic_creation
            topicMetadataRequest.setAllowAutoTopicCreation(buffer.readBoolean());
        }
        return topicMetadataRequest;
    }

    @Override
    public void encode(TopicMetadataResponse payload, ByteBuf buffer) throws Exception {
        short version = payload.getVersion();
        if (version >= 3) {
            // throttle_time_ms
            buffer.writeInt(payload.getThrottleTimeMs());
        }

        List<KafkaBroker> brokers = payload.getBrokers();
        buffer.writeInt(brokers.size());
        for (KafkaBroker broker : brokers) {
            buffer.writeInt(broker.getId());
            try {
                Serializer.write(broker.getHost(), buffer, Serializer.SHORT_SIZE);
            } catch (Exception e) {
                throw new TransportException.CodecException(e);
            }
            buffer.writeInt(broker.getPort());

            if (version >= 1) {
                // rack
                try {
                    Serializer.write(StringUtils.EMPTY, buffer, Serializer.SHORT_SIZE);
                } catch (Exception e) {
                    throw new TransportException.CodecException(e);
                }
            }
        }

        if (version >= 2) {
            try {
                Serializer.write(payload.getClusterId(), buffer, Serializer.SHORT_SIZE);
            } catch (Exception e) {
                throw new TransportException.CodecException(e);
            }
        }
        if (version >= 1) {
            // controller id
            buffer.writeInt(-1);
        }

        List<KafkaTopicMetadata> kafkaTopicMetadatas = payload.getTopicMetadatas();
        buffer.writeInt(kafkaTopicMetadatas.size());
        for (KafkaTopicMetadata kafkaTopicMetadata : kafkaTopicMetadatas) {
            buffer.writeShort(kafkaTopicMetadata.getErrorCode());
            try {
                Serializer.write(kafkaTopicMetadata.getTopic(), buffer, Serializer.SHORT_SIZE);
            } catch (Exception e) {
                throw new TransportException.CodecException(e);
            }

            if (version >= 1) {
                // is_internal
                buffer.writeBoolean(false);
            }

            List<KafkaPartitionMetadata> kafkaPartitionMetadatas = kafkaTopicMetadata.getKafkaPartitionMetadata();
            buffer.writeInt(kafkaPartitionMetadatas.size());
            for (KafkaPartitionMetadata kafkaPartitionMetadata : kafkaPartitionMetadatas) {
                buffer.writeShort(KafkaErrorCode.NONE.getCode());
                buffer.writeInt(kafkaPartitionMetadata.getPartition());
                KafkaBroker leaderBroker = kafkaPartitionMetadata.getLeader();
                if (leaderBroker != null) {
                    buffer.writeInt(leaderBroker.getId());
                } else {
                    buffer.writeInt(-1);
                }
                List<KafkaBroker> replicas = kafkaPartitionMetadata.getReplicas();
                buffer.writeInt(replicas.size());
                for (KafkaBroker replica : replicas) {
                    buffer.writeInt(replica.getId());
                }
                List<KafkaBroker> isrs = kafkaPartitionMetadata.getIsr();
                buffer.writeInt(isrs.size());
                for (KafkaBroker isr : isrs) {
                    buffer.writeInt(isr.getId());
                }
                if (version >= 5) {
                    // offline replicas
                    Set<Integer> offlineReplicas = new HashSet<Integer>();
                    buffer.writeInt(offlineReplicas.size());
                    for (int replicaId : offlineReplicas) {
                        buffer.writeInt(replicaId);
                    }
                }
            }
        }
    }

    @Override
    public int type() {
        return KafkaCommandType.METADATA.getCode();
    }
}