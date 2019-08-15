package io.chubao.joyqueue.broker.protocol.coordinator.assignment.domain;

/**
 * PartitionAssignmentMetadata
 *
 * author: gaohaoxiang
 * date: 2018/12/5
 */
public class PartitionAssignmentMetadata {

    private short partition;
    private int assigned;

    public int incrAssigned() {
        return assigned++;
    }

    public int decrAssigned() {
        return assigned--;
    }

    public void setPartition(short partition) {
        this.partition = partition;
    }

    public short getPartition() {
        return partition;
    }

    public void setAssigned(int assigned) {
        this.assigned = assigned;
    }

    public int getAssigned() {
        return assigned;
    }
}