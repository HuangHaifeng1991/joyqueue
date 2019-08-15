package io.chubao.joyqueue.broker.network.traffic;

/**
 * FetchTrafficPayload
 *
 * author: gaohaoxiang
 * date: 2019/5/16
 */
public interface FetchTrafficPayload extends TrafficPayload, TrafficType {

    String TYPE = "fetch";

    @Override
    default String getTrafficType() {
        return TYPE;
    }
}