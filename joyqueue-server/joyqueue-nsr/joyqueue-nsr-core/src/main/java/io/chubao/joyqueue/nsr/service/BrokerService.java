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
package io.chubao.joyqueue.nsr.service;

import io.chubao.joyqueue.domain.Broker;
import io.chubao.joyqueue.model.PageResult;
import io.chubao.joyqueue.model.QPageQuery;
import io.chubao.joyqueue.nsr.model.BrokerQuery;

import java.util.List;

/**
 * @author lixiaobin6
 * 下午3:11 2018/8/13
 */
public interface BrokerService {

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    Broker getById(long id);

    /**
     * 根据IP和端口获取Broker
     *
     * @param brokerIp
     * @param brokerPort
     * @return
     */
    Broker getByIpAndPort(String brokerIp, Integer brokerPort);

    /**
     * 根据重试类型查询broker
     * @param retryType
     * @return
     */
    List<Broker> getByRetryType(String retryType);

    /**
     * 根据ids 查询所有broker集合
     * @param ids
     * @return
     */
    List<Broker> getByIds(List<Long> ids);

    /**
     * 获取全部
     * @return
     */
    List<Broker> getAll();

    /**
     * 搜索
     *
     * @param pageQuery
     * @return
     */
    PageResult<Broker> search(QPageQuery<BrokerQuery> pageQuery);

    /**
     * 更新
     * @param broker
     */
    Broker add(Broker broker);

    /**
     * 更新
     * @param broker
     */
    Broker update(Broker broker);

    /**
     * 根据model删除
     *
     * @param id
     */
    void delete(long id);
}