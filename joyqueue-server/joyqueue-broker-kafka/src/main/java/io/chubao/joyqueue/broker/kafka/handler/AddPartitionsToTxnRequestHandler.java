package io.chubao.joyqueue.broker.kafka.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.chubao.joyqueue.broker.kafka.KafkaCommandHandler;
import io.chubao.joyqueue.broker.kafka.KafkaCommandType;
import io.chubao.joyqueue.broker.kafka.KafkaContext;
import io.chubao.joyqueue.broker.kafka.KafkaContextAware;
import io.chubao.joyqueue.broker.kafka.KafkaErrorCode;
import io.chubao.joyqueue.broker.kafka.command.AddPartitionsToTxnRequest;
import io.chubao.joyqueue.broker.kafka.command.AddPartitionsToTxnResponse;
import io.chubao.joyqueue.broker.kafka.coordinator.transaction.TransactionCoordinator;
import io.chubao.joyqueue.broker.kafka.coordinator.transaction.exception.TransactionException;
import io.chubao.joyqueue.broker.kafka.helper.KafkaClientHelper;
import io.chubao.joyqueue.broker.kafka.model.PartitionMetadataAndError;
import io.chubao.joyqueue.network.transport.Transport;
import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.network.transport.command.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * AddPartitionsToTxnRequestHandler
 *
 * author: gaohaoxiang
 * date: 2019/4/4
 */
public class AddPartitionsToTxnRequestHandler implements KafkaCommandHandler, Type, KafkaContextAware {

    protected static final Logger logger = LoggerFactory.getLogger(AddPartitionsToTxnRequestHandler.class);

    private TransactionCoordinator transactionCoordinator;

    @Override
    public void setKafkaContext(KafkaContext kafkaContext) {
        this.transactionCoordinator = kafkaContext.getTransactionCoordinator();
    }

    @Override
    public Command handle(Transport transport, Command command) {
        AddPartitionsToTxnRequest addPartitionsToTxnRequest = (AddPartitionsToTxnRequest) command.getPayload();
        String clientId = KafkaClientHelper.parseClient(addPartitionsToTxnRequest.getClientId());
        AddPartitionsToTxnResponse response = null;

        try {
            Map<String, List<PartitionMetadataAndError>> errors = transactionCoordinator.handleAddPartitionsToTxn(clientId, addPartitionsToTxnRequest.getTransactionId(),
                    addPartitionsToTxnRequest.getProducerId(), addPartitionsToTxnRequest.getProducerEpoch(), addPartitionsToTxnRequest.getPartitions());
            response = new AddPartitionsToTxnResponse(errors);
        } catch (TransactionException e) {
            logger.warn("add partitions to txn exception, code: {}, message: {}, request: {}", e.getCode(), e.getMessage(), addPartitionsToTxnRequest);
            response = new AddPartitionsToTxnResponse(buildPartitionError(e.getCode(), addPartitionsToTxnRequest.getPartitions()));
        } catch (Exception e) {
            logger.error("add partitions to txn exception, request: {}", addPartitionsToTxnRequest, e);
            response = new AddPartitionsToTxnResponse(buildPartitionError(KafkaErrorCode.exceptionFor(e), addPartitionsToTxnRequest.getPartitions()));
        }

        return new Command(response);
    }

    protected Map<String, List<PartitionMetadataAndError>> buildPartitionError(int code, Map<String, List<Integer>> partitions) {
        Map<String, List<PartitionMetadataAndError>> result = Maps.newHashMapWithExpectedSize(partitions.size());
        for (Map.Entry<String, List<Integer>> entry : partitions.entrySet()) {
            List<PartitionMetadataAndError> partitionMetadataAndErrors = Lists.newArrayListWithCapacity(entry.getValue().size());
            for (Integer partition : entry.getValue()) {
                partitionMetadataAndErrors.add(new PartitionMetadataAndError(partition, (short) code));
            }
            result.put(entry.getKey(), partitionMetadataAndErrors);
        }
        return result;
    }

    @Override
    public int type() {
        return KafkaCommandType.ADD_PARTITIONS_TO_TXN.getCode();
    }
}