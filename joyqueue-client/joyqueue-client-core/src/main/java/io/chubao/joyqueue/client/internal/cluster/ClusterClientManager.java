package io.chubao.joyqueue.client.internal.cluster;

import io.chubao.joyqueue.client.internal.nameserver.NameServerConfig;
import io.chubao.joyqueue.client.internal.nameserver.NameServerConfigChecker;
import io.chubao.joyqueue.client.internal.transport.Client;
import io.chubao.joyqueue.client.internal.transport.ClientManager;
import io.chubao.joyqueue.client.internal.transport.config.TransportConfig;
import io.chubao.joyqueue.network.domain.BrokerNode;
import io.chubao.joyqueue.toolkit.URL;
import io.chubao.joyqueue.toolkit.service.Service;

/**
 * ClusterClientManager
 *
 * author: gaohaoxiang
 * date: 2018/11/28
 */
public class ClusterClientManager extends Service {

    private TransportConfig transportConfig;
    private NameServerConfig nameServerConfig;

    private BrokerNode bootstrapNode;
    private ClientManager clientManager;

    public ClusterClientManager(TransportConfig transportConfig, NameServerConfig nameServerConfig) {
        NameServerConfigChecker.check(nameServerConfig);

        this.transportConfig = transportConfig;
        this.nameServerConfig = nameServerConfig;
    }

    public ClusterClient getOrCreateClient() {
        return getOrCreateClient(bootstrapNode);
    }

    public ClusterClient doGetOrCreateClient() {
        Client client = clientManager.doGetOrCreateClient(bootstrapNode);
        return ClusterClient.build(client, transportConfig, nameServerConfig);
    }

    public ClusterClient createClient(BrokerNode node) {
        Client client = clientManager.createClient(node);
        return new ClusterClient(client, transportConfig, nameServerConfig);
    }

    public ClusterClient getOrCreateClient(BrokerNode node) {
        Client client = clientManager.getOrCreateClient(node);
        return ClusterClient.build(client, transportConfig, nameServerConfig);
    }

    public ClusterClient getClient(BrokerNode node) {
        Client client = clientManager.getClient(node);
        if (client == null) {
            return null;
        }
        return ClusterClient.build(client, transportConfig, nameServerConfig);
    }

    @Override
    protected void validate() throws Exception {
        transportConfig = transportConfig.copy();
        transportConfig.setConnections(1);

        URL url = URL.valueOf(String.format("joyqueue://%s", nameServerConfig.getAddress()));
        if (url.getPort() == 0) {
            bootstrapNode = new BrokerNode(url.getHost(), -1);
        } else {
            bootstrapNode = new BrokerNode(url.getHost(), url.getPort());
        }
        clientManager = new ClientManager(transportConfig, nameServerConfig);
    }

    @Override
    protected void doStart() throws Exception {
        clientManager.start();
    }

    @Override
    protected void doStop() {
        if (clientManager != null) {
            clientManager.stop();
        }
    }
}