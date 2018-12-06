/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cloudstack.storage.datastore.lifecycle;

import com.cloud.agent.api.StoragePoolInfo;
import com.cloud.capacity.CapacityManager;
import com.cloud.dc.ClusterDetailsDao;
import com.cloud.dc.dao.ClusterDao;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.host.HostVO;
import com.cloud.host.dao.HostDao;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.resource.ResourceManager;
import com.cloud.storage.StorageManager;
import com.cloud.storage.StoragePool;
import com.cloud.storage.StoragePoolAutomation;
import com.ixsystems.vcp.entities.Dataset;
import org.apache.cloudstack.engine.subsystem.api.storage.ClusterScope;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStore;
import org.apache.cloudstack.engine.subsystem.api.storage.EndPoint;
import org.apache.cloudstack.engine.subsystem.api.storage.EndPointSelector;
import org.apache.cloudstack.engine.subsystem.api.storage.HostScope;
import org.apache.cloudstack.engine.subsystem.api.storage.PrimaryDataStoreLifeCycle;
import org.apache.cloudstack.engine.subsystem.api.storage.ZoneScope;
import org.apache.cloudstack.storage.command.AttachPrimaryDataStoreCmd;
import org.apache.cloudstack.storage.command.CreatePrimaryDataStoreCmd;
import org.apache.cloudstack.storage.datastore.PrimaryDataStoreProviderManager;
import org.apache.cloudstack.storage.datastore.db.PrimaryDataStoreDao;
import org.apache.cloudstack.storage.volume.datastore.PrimaryDataStoreHelper;
import org.apache.log4j.Logger;
import org.freenas.client.connectors.rest.imp.AuthenticationConnector;
import org.freenas.client.connectors.rest.imp.EndpointConnector;
import org.freenas.client.storage.rest.impl.DatasetRestConnector;
import org.freenas.client.storage.rest.impl.SharingNFSRestConnector;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeNASPrimaryDataStoreLifeCycleImpl extends CloudStackPrimaryDataStoreLifeCycleImpl implements PrimaryDataStoreLifeCycle {

    private static final Logger logger =
            Logger.getLogger(FreeNASPrimaryDataStoreLifeCycleImpl.class);

    @Inject
    EndPointSelector selector;
    @Inject
    PrimaryDataStoreDao dataStoreDao;
    @Inject
    HostDao hostDao;

    @Inject private PrimaryDataStoreDao _storagePoolDao;

    @Inject private CapacityManager _capacityMgr;


    private String username = "";
    private String password = "";
    private String endpoint = "";


    @Inject
    private PrimaryDataStoreHelper dataStoreHelper;
    @Inject
    private ResourceManager _resourceMgr;

    @Inject
    private ClusterDao _clusterDao;
    @Inject
    private ClusterDetailsDao _clusterDetailsDao;

    @Inject
    StorageManager _storageMgr;
    @Inject
    private StoragePoolAutomation storagePoolAutomation;

    @Inject
    PrimaryDataStoreHelper primaryStoreHelper;
    @Inject
    PrimaryDataStoreProviderManager providerMgr;

    @Inject
    private DataCenterDao zoneDao;


    public FreeNASPrimaryDataStoreLifeCycleImpl() {
    }

    @Override
    public DataStore initialize(Map<String, Object> dsInfos) {



/*

        String url = (String) dsInfos.get("url");
        Long zoneId = (Long) dsInfos.get("zoneId");
        Long podId = (Long)dsInfos.get("podId");
        Long clusterId = (Long)dsInfos.get("clusterId");
        String storagePoolName = (String) dsInfos.get("name");
        String providerName = (String) dsInfos.get("providerName");
        Long capacityBytes = (Long)dsInfos.get("capacityBytes");
        Long capacityIops = (Long)dsInfos.get("capacityIops");
        String tags = (String)dsInfos.get("tags");
        Map<String, String> details = (Map<String, String>) dsInfos.get("details");
        DataCenterVO zone = zoneDao.findById(zoneId);

        // Fix here ;

        if (podId != null && clusterId == null) {
            throw new CloudRuntimeException("If the Pod ID is specified, the Cluster ID must also be specified.");
        }

        if (podId == null && clusterId != null) {
            throw new CloudRuntimeException("If the Pod ID is not specified, the Cluster ID must also not be specified.");
        }


        if (capacityBytes == null || capacityBytes <= 0) {
            throw new IllegalArgumentException("'capacityBytes' must be present and greater than 0.");
        }

        if (capacityIops == null || capacityIops <= 0) {
            throw new IllegalArgumentException("'capacityIops' must be present and greater than 0.");
        }



        // How to check what kind of protocol?

        PrimaryDataStoreParameters parameters = new PrimaryDataStoreParameters();

        String username = IXsystemsUtil.getUsername(url);
        String password = IXsystemsUtil.getPassword(url);
        String freeNasAPI = IXsystemsUtil.getParsedUrl(url);
        String host = IXsystemsUtil.getHost(url);
        Integer port = IXsystemsUtil.getPort(url);

        //

        parameters.setHost(host);
        parameters.setPort(port);
        parameters.setPath(url);
        parameters.setType(Storage.StoragePoolType.NetworkFilesystem); // FIXME
        parameters.setUuid(UUID.randomUUID().toString());
        parameters.setZoneId(zoneId);
        parameters.setPodId(podId);
        parameters.setClusterId(clusterId);
        parameters.setName(storagePoolName);
        parameters.setProviderName(providerName);
        parameters.setManaged(true);
        parameters.setCapacityBytes(capacityBytes);
        parameters.setUsedBytes(0);
        parameters.setCapacityIops(capacityIops);

        if (clusterId != null) {
            ClusterVO clusterVO = _clusterDao.findById(clusterId);

            Preconditions.checkNotNull(clusterVO, "Unable to locate the specified cluster");

            parameters.setHypervisorType(clusterVO.getHypervisorType());
        }
        else {
            parameters.setHypervisorType(HypervisorType.Any);
        }

        parameters.setTags(tags);
        details.put(IXsystemsUtil.IXSYSTEMS_URL, freeNasAPI);
        details.put(IXsystemsUtil.IXSYSTEMS_USERNAME, username);
        details.put(IXsystemsUtil.IXSYSTEMS_PASSWORD, password);


        parameters.setDetails(details);

        DataStore store = primaryStoreHelper.createPrimaryDataStore(parameters);*/
        //return store;

        //String name = "ps01";
        String name = (String) dsInfos.get("name");
        String path = "/mnt/dev01/"+name;

        String url = (String) dsInfos.get("url");

        String [] aux1 = url.split("://");
        String [] aux2 = aux1[1].split(("/"));

        String protocol = aux1[0];
        String server = aux2[0];
        String volumeName = aux2[1];
        String dataset = name;

        String username = "root";
        String password = "password";
        String protocolFN = "http";
        String portFN = "80";
        this.endpoint = server; // how about the port?
        if (url.contains("@")){

            // this is for parse the username, password
            url = url.split("@")[1];
            username =  url.split("@")[0].split(":")[0];
            password =  url.split("@")[0].split(":")[1];
            this.username = username;
            this.password = password;

        }


        createiXSystemDataset(name, volumeName);
        createiXSystemNFS(path,name);

        //"nfs://username:password@192.168.100.14/mnt/dev01/ds01"

        return super.initialize(dsInfos);
    }




    public AuthenticationConnector getAuth(){
        AuthenticationConnector auth = new AuthenticationConnector(this.username, this.password);
        return auth;
    }

    public EndpointConnector getEndPointConnector(){
        EndpointConnector ep = new EndpointConnector("http://192.168.100.14", "http");
        return ep;
    }


    private DatasetRestConnector getDSConnector(){
        AuthenticationConnector auth = getAuth();


        EndpointConnector ep = getEndPointConnector();
        DatasetRestConnector gs = new DatasetRestConnector(ep, auth);

        return gs;
    }

    /**
     * The idea here is to create a dataset
     * @param path
     */
    protected void createiXSystemDataset(String name, String volume){
        boolean result = false;
        DatasetRestConnector gs = getDSConnector();
        Map<String, String> args = new HashMap<String, String>();

        args.put("name", name);

        Dataset ds = null ;
        try {
            ds = gs.create(volume, args);
            result = true;
        }
        catch (Exception e){
            logger.error("Error while creating dataset", e);
        }
    }

    protected void createiXSystemNFS(String path, String comment){


        SharingNFSRestConnector connector = new SharingNFSRestConnector(getEndPointConnector(), getAuth());
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("nfs_comment", "comment");
        List<String> paths = new ArrayList<>();
        paths.add(path);
        args.put("nfs_paths",paths);
        args.put("nfs_security", "sys");

        connector.create(path, args);
    }



    protected void attachCluster2(DataStore store) {
        // send down AttachPrimaryDataStoreCmd command to all the hosts in the
        // cluster


        List<EndPoint> endPoints = selector.selectAll(store);
        CreatePrimaryDataStoreCmd createCmd = new CreatePrimaryDataStoreCmd(store.getUri());
        EndPoint ep = endPoints.get(0);
        HostVO host = hostDao.findById(ep.getId());
        if (host.getHypervisorType() == HypervisorType.XenServer) {
            ep.sendMessage(createCmd);
        }

        endPoints.get(0).sendMessage(createCmd);
        AttachPrimaryDataStoreCmd cmd = new AttachPrimaryDataStoreCmd(store.getUri());
        for (EndPoint endp : endPoints) {
            endp.sendMessage(cmd);
        }

        //return super.attachCluster(store);




    }

    @Override
    public boolean attachCluster(DataStore dataStore, ClusterScope scope) {
        /*
        StoragePoolVO dataStoreVO = dataStoreDao.findById(dataStore.getId());
        dataStoreVO.setDataCenterId(scope.getZoneId());
        dataStoreVO.setPodId(scope.getPodId());
        dataStoreVO.setClusterId(scope.getScopeId());
        dataStoreVO.setStatus(StoragePoolStatus.Attaching);
        dataStoreVO.setScope(scope.getScopeType());
        dataStoreDao.update(dataStoreVO.getId(), dataStoreVO);

        attachCluster(dataStore);

        dataStoreVO = dataStoreDao.findById(dataStore.getId());
        dataStoreVO.setStatus(StoragePoolStatus.Up);
        dataStoreDao.update(dataStoreVO.getId(), dataStoreVO);

        return true;
        */

        return super.attachCluster(dataStore, scope);

    }

    @Override
    public boolean attachZone(DataStore dataStore, ZoneScope scope, HypervisorType hypervisorType) {
        /*

        dataStoreHelper.attachZone(dataStore);

        List<HostVO> xenServerHosts = _resourceMgr.listAllUpAndEnabledHostsInOneZoneByHypervisor(Hypervisor.HypervisorType.XenServer, scope.getScopeId());
        List<HostVO> vmWareServerHosts = _resourceMgr.listAllUpAndEnabledHostsInOneZoneByHypervisor(Hypervisor.HypervisorType.VMware, scope.getScopeId());
        List<HostVO> kvmHosts = _resourceMgr.listAllUpAndEnabledHostsInOneZoneByHypervisor(Hypervisor.HypervisorType.KVM, scope.getScopeId());
        List<HostVO> hosts = new ArrayList<HostVO>();

        hosts.addAll(xenServerHosts);
        hosts.addAll(vmWareServerHosts);
        hosts.addAll(kvmHosts);


        for (HostVO host : hosts) {
            try {
                _storageMgr.connectHostToSharedPool(host.getId(), dataStore.getId());
            } catch (Exception e) {
                logger.warn("Unable to establish a connection between " + host + " and " + dataStore, e);
            }
        }

        return true;
        */

        return super.attachZone(dataStore, scope, hypervisorType);

    }

    @Override
    public boolean attachHost(DataStore store, HostScope scope, StoragePoolInfo existingInfo) {
        //return true;
        return super.attachHost(store, scope, existingInfo);
    }

    @Override
    public boolean maintain(DataStore store) {

        // Can we call something in iXsystems to be at mantainance mode also?
        // or should it do it?
        /*
        storagePoolAutomation.maintain(store);
        dataStoreHelper.maintain(store);

        return true;*/
        return super.maintain(store);

    }

    @Override
    public boolean cancelMaintain(DataStore store) {
        /*dataStoreHelper.cancelMaintain(store);
        storagePoolAutomation.cancelMaintain(store);
        return true;*/
        return super.cancelMaintain(store);

    }

    @Override
    public boolean deleteDataStore(DataStore store) {
        //return dataStoreHelper.deletePrimaryDataStore(store);
        return super.deleteDataStore(store);
    }

    /* (non-Javadoc)
     * @see org.apache.cloudstack.engine.subsystem.api.storage.DataStoreLifeCycle#migrateToObjectStore(org.apache.cloudstack.engine.subsystem.api.storage.DataStore)
     */
    @Override
    public boolean migrateToObjectStore(DataStore store) {
        //return false;
        return super.migrateToObjectStore(store);
    }

    @Override
    public void updateStoragePool(StoragePool storagePool, Map<String, String> details) {
        /*

        StoragePoolVO storagePoolVo = _storagePoolDao.findById(storagePool.getId());

        String strCapacityBytes = details.get(PrimaryDataStoreLifeCycle.CAPACITY_BYTES);
        Long capacityBytes = strCapacityBytes != null ? Long.parseLong(strCapacityBytes) : null;

        if (capacityBytes != null) {
            long usedBytes = _capacityMgr.getUsedBytes(storagePoolVo);

            if (capacityBytes < usedBytes) {
                throw new CloudRuntimeException("Cannot reduce the number of bytes for this storage pool as it would lead to an insufficient number of bytes");
            }
        }

        String strCapacityIops = details.get(PrimaryDataStoreLifeCycle.CAPACITY_IOPS);
        Long capacityIops = strCapacityIops != null ? Long.parseLong(strCapacityIops) : null;

        if (capacityIops != null) {
            long usedIops = _capacityMgr.getUsedIops(storagePoolVo);

            if (capacityIops < usedIops) {
                throw new CloudRuntimeException("Cannot reduce the number of IOPS for this storage pool as it would lead to an insufficient number of IOPS");
            }
        }
        */
        super.updateStoragePool(storagePool, details);


    }

    @Override
    public void enableStoragePool(DataStore store) {
        //dataStoreHelper.enable(store);
        super.enableStoragePool(store);

    }

    @Override
    public void disableStoragePool(DataStore store) {
        //dataStoreHelper.disable(store);
        super.disableStoragePool(store);

    }
}
