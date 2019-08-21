/**
 * Copyright 2019 The JoyQueue Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chubao.joyqueue.service.impl;

import io.chubao.joyqueue.exception.ServiceException;
import io.chubao.joyqueue.model.domain.PartitionGroupReplica;
import io.chubao.joyqueue.model.domain.TopicPartitionGroup;
import io.chubao.joyqueue.nsr.BrokerNameServerService;
import io.chubao.joyqueue.nsr.ReplicaServerService;
import io.chubao.joyqueue.nsr.TopicNameServerService;
import io.chubao.joyqueue.service.BrokerGroupRelatedService;
import io.chubao.joyqueue.service.PartitionGroupReplicaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

import static io.chubao.joyqueue.exception.ServiceException.INTERNAL_SERVER_ERROR;

/**
 * 主题Broker分组关联关系 服务实现
 * Created by chenyanying3 on 2018-10-18
 */
@Service("partitionGroupReplicaService")
public class PartitionGroupReplicaServiceImpl  implements PartitionGroupReplicaService {
    private final Logger logger = LoggerFactory.getLogger(PartitionGroupReplicaServiceImpl.class);
    @Autowired
    private TopicNameServerService topicNameServerService;
    @Autowired
    private ReplicaServerService replicaServerService;
    @Autowired
    private BrokerNameServerService brokerNameServerService;
    @Autowired
    private BrokerGroupRelatedService brokerGroupRelatedService;

    @Override
    public int addWithNameservice(PartitionGroupReplica replica, TopicPartitionGroup partitionGroup) {
        int result = 1;
        partitionGroup.getReplicaGroups().add(replica);
        try {
            topicNameServerService.updatePartitionGroup(partitionGroup);
        } catch (Exception e) {
            String errorMsg = "topic"+replica.getTopic().getCode()+"扩容,同步NameServer失败";
            logger.error(errorMsg, e);
            throw new ServiceException(INTERNAL_SERVER_ERROR, errorMsg);//回滚
        }
        return result;
    }

    @Override
    public int removeWithNameservice(PartitionGroupReplica replica, TopicPartitionGroup partitionGroup) {
        int result = 1;
        for(Iterator<PartitionGroupReplica> it = partitionGroup.getReplicaGroups().iterator();it.hasNext();){
            if(it.next().getBrokerId()==replica.getBrokerId())it.remove();
        }
        try {
            topicNameServerService.updatePartitionGroup(partitionGroup);
        } catch (Exception e) {
            String errorMsg = "topic"+replica.getTopic().getCode()+"缩容,同步NameServer失败";
            logger.error(errorMsg, e);
            throw new ServiceException(INTERNAL_SERVER_ERROR, errorMsg);//回滚
        }
        return result;
    }

    @Override
    public PartitionGroupReplica findById(String s) throws Exception {
        return replicaServerService.findById(s);
    }

    @Override
    public List<PartitionGroupReplica> getByTopic(String topic, String namespace) throws Exception {
        return replicaServerService.findByTopic(topic, namespace);
    }

    @Override
    public int delete(PartitionGroupReplica model) throws Exception {
        return replicaServerService.delete(model);
    }

    @Override
    public int add(PartitionGroupReplica model) throws Exception {
        return replicaServerService.add(model);
    }

    @Override
    public int update(PartitionGroupReplica model) throws Exception {
        return replicaServerService.update(model);
    }
}
