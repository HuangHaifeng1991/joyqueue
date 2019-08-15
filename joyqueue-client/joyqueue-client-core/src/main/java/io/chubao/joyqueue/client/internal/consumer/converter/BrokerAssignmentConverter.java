package io.chubao.joyqueue.client.internal.consumer.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.chubao.joyqueue.client.internal.consumer.coordinator.domain.BrokerAssignment;
import io.chubao.joyqueue.client.internal.consumer.coordinator.domain.BrokerAssignments;
import io.chubao.joyqueue.client.internal.consumer.coordinator.domain.PartitionAssignment;
import io.chubao.joyqueue.client.internal.metadata.domain.PartitionMetadata;
import io.chubao.joyqueue.client.internal.metadata.domain.TopicMetadata;
import io.chubao.joyqueue.network.domain.BrokerNode;

import java.util.List;
import java.util.Map;

/**
 * BrokerAssignmentConverter
 *
 * author: gaohaoxiang
 * date: 2018/12/17
 */
public class BrokerAssignmentConverter {

    public static BrokerAssignments convertBrokerAssignments(TopicMetadata topicMetadata) {
        BrokerAssignments result = new BrokerAssignments();
        Map<BrokerNode, List<Short>> brokerPartitions = Maps.newHashMap();

        for (PartitionMetadata partitionMetadata : topicMetadata.getPartitions()) {
            if (partitionMetadata.getLeader() == null || !partitionMetadata.getLeader().isReadable()) {
                continue;
            }
            List<Short> brokerPartitionList = brokerPartitions.get(partitionMetadata.getLeader());
            if (brokerPartitionList == null) {
                brokerPartitionList = Lists.newLinkedList();
                brokerPartitions.put(partitionMetadata.getLeader(), brokerPartitionList);
            }
            brokerPartitionList.add(partitionMetadata.getId());
        }

        List<BrokerAssignment> assignments = Lists.newArrayListWithCapacity(brokerPartitions.size());
        for (Map.Entry<BrokerNode, List<Short>> brokerEntry : brokerPartitions.entrySet()) {
            assignments.add(new BrokerAssignment(brokerEntry.getKey(), new PartitionAssignment(brokerEntry.getValue())));
        }

        result.setAssignments(assignments);
        return result;
    }

    public static BrokerAssignments convertTopicAssignments(TopicMetadata topicMetadata) {
        List<BrokerAssignment> assignments = Lists.newArrayListWithCapacity(topicMetadata.getPartitions().size());
        for (PartitionMetadata partitionMetadata : topicMetadata.getPartitions()) {
            if (partitionMetadata.getLeader() == null || !partitionMetadata.getLeader().isReadable()) {
                continue;
            }
            assignments.add(new BrokerAssignment(partitionMetadata.getLeader(),
                    new PartitionAssignment(Lists.newArrayList(partitionMetadata.getId()))));
        }
        BrokerAssignments brokerAssignments = new BrokerAssignments();
        brokerAssignments.setAssignments(assignments);
        return brokerAssignments;
    }
}