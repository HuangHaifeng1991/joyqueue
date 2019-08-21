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
package io.chubao.joyqueue.nsr.ignite.service;

import com.google.inject.Inject;
import io.chubao.joyqueue.domain.Config;
import io.chubao.joyqueue.event.ConfigEvent;
import io.chubao.joyqueue.event.MetaEvent;
import io.chubao.joyqueue.nsr.ignite.dao.ConfigDao;
import io.chubao.joyqueue.nsr.ignite.model.IgniteConfig;
import io.chubao.joyqueue.nsr.message.Messenger;
import io.chubao.joyqueue.nsr.service.ConfigService;
import org.apache.ignite.Ignition;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wylixiaobin
 * Date: 2018/9/4
 */
public class IgniteConfigService implements ConfigService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigDao configDao;

    @Inject
    public IgniteConfigService(ConfigDao configDao) {
        this.configDao = configDao;
    }

    @Inject
    protected Messenger messenger;

    public Config getByGroupAndKey(String group, String key) {
        return getById(IgniteConfig.getId(group, key));
    }

    @Override
    public Config add(Config config) {
        try (Transaction tx = Ignition.ignite().transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.READ_COMMITTED)) {
            configDao.addOrUpdate(new IgniteConfig(config));
            this.publishEvent(ConfigEvent.add(config.getGroup(), config.getKey(), config.getValue()));
            tx.commit();
            return config;
        } catch (Exception e) {
            String message = String.format("add config group [%s] key [%s] error", config.getGroup(), config.getKey());
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Config update(Config config) {
        try (Transaction tx = Ignition.ignite().transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.READ_COMMITTED)) {
            configDao.addOrUpdate(new IgniteConfig(config));
            this.publishEvent(ConfigEvent.update(config.getGroup(), config.getKey(), config.getValue()));
            tx.commit();
            return config;
        } catch (Exception e) {
            String message = String.format("update config group [%s] key [%s] error", config.getGroup(), config.getKey());
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public void delete(String id) {
        IgniteConfig config = configDao.findById(id);
        try (Transaction tx = Ignition.ignite().transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.READ_COMMITTED)) {
            configDao.deleteById(id);
            this.publishEvent(ConfigEvent.remove(config.getGroup(), config.getKey(), config.getValue()));
            tx.commit();
        } catch (Exception e) {
            String message = String.format("remove config group [%s] key [%s] error", config.getGroup(), config.getKey());
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    public void publishEvent(MetaEvent event) {
        messenger.publish(event);
    }

    public IgniteConfig toIgniteModel(Config model) {
        return new IgniteConfig(model);
    }

    @Override
    public Config getById(String id) {
        return configDao.findById(id);
    }

    @Override
    public List<Config> getAll() {
        return convert(configDao.list(null));
    }

    private List<Config> convert(List<IgniteConfig> iConfigs) {
        if (iConfigs == null) {
            return Collections.emptyList();
        }

        List<Config> configs = new ArrayList<>();
        configs.addAll(iConfigs);
        return configs;
    }
}



