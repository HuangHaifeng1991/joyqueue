package io.chubao.joyqueue.client.internal.consumer.support;

import io.chubao.joyqueue.client.internal.consumer.BrokerLoadBalance;
import io.chubao.joyqueue.client.internal.consumer.coordinator.domain.BrokerAssignment;
import io.chubao.joyqueue.client.internal.consumer.coordinator.domain.BrokerAssignments;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * RandomBrokerLoadBalance
 *
 * author: gaohaoxiang
 * date: 2018/12/12
 */
public class RandomBrokerLoadBalance implements BrokerLoadBalance {

    public static final String NAME = "random";

    @Override
    public BrokerAssignment loadBalance(BrokerAssignments brokerAssignments) {
        List<BrokerAssignment> assignments = brokerAssignments.getAssignments();
        return assignments.get(RandomUtils.nextInt(0, assignments.size()));
    }

    @Override
    public String type() {
        return NAME;
    }

}