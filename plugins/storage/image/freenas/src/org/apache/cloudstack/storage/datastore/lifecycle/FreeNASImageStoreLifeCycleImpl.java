// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.storage.datastore.lifecycle;

import com.cloud.agent.api.StoragePoolInfo;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.ixsystems.vcp.entities.Dataset;
import org.apache.cloudstack.engine.subsystem.api.storage.ClusterScope;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStore;
import org.apache.cloudstack.engine.subsystem.api.storage.HostScope;
import org.apache.cloudstack.engine.subsystem.api.storage.ZoneScope;
import org.apache.cloudstack.storage.datastore.db.ImageStoreDao;
import org.apache.cloudstack.storage.image.datastore.ImageStoreHelper;
import org.apache.cloudstack.storage.image.datastore.ImageStoreProviderManager;
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

/**
 *
 */
public class FreeNASImageStoreLifeCycleImpl extends CloudStackImageStoreLifeCycleImpl {//implements ImageStoreLifeCycle {

    private static final Logger logger = Logger.getLogger(FreeNASImageStoreLifeCycleImpl.class);

    @Inject
    protected ImageStoreDao imageStoreDao;
    @Inject
    ImageStoreHelper imageStoreHelper;
    @Inject
    ImageStoreProviderManager imageStoreMgr;

    public FreeNASImageStoreLifeCycleImpl() {
    }

    @Override
    public DataStore initialize(Map<String, Object> dsInfos) {

        /**
        String url = (String)dsInfos.get("url");
        String name = (String)dsInfos.get("name");
        String providerName = (String)dsInfos.get("providerName");
        ScopeType scope = (ScopeType)dsInfos.get("scope");
        DataStoreRole role = (DataStoreRole)dsInfos.get("role");
        Map<String, String> details = (Map<String, String>)dsInfos.get("details");

        s_logger.info("Trying to add a S3 store with endpoint: " + details.get(ApiConstants.S3_END_POINT));

        Map<String, Object> imageStoreParameters = new HashMap();
        imageStoreParameters.put("name", name);
        imageStoreParameters.put("url", url);
        String protocol = "http";
        String useHttps = details.get(ApiConstants.S3_HTTPS_FLAG);
        if (useHttps != null && Boolean.parseBoolean(useHttps)) {
            protocol = "https";
        }
        imageStoreParameters.put("protocol", protocol);
        if (scope != null) {
            imageStoreParameters.put("scope", scope);
        } else {
            imageStoreParameters.put("scope", ScopeType.REGION);
        }
        imageStoreParameters.put("providerName", providerName);
        imageStoreParameters.put("role", role);

        ImageStoreVO ids = imageStoreHelper.createImageStore(imageStoreParameters, details);

        return imageStoreMgr.getImageStore(ids.getId());
         **/

        String name = (String) dsInfos.get("name");
        String path = "/mnt/dev01/"+name;

        //String url = (String) dsInfos.get("url");
        createiXSystemDataset(name, "dev01");
        createiXSystemNFS(path,name);


        return super.initialize(dsInfos);
    }




    public AuthenticationConnector getAuth(){
        AuthenticationConnector auth = new AuthenticationConnector("root", "");
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



    @Override
    public boolean attachCluster(DataStore store, ClusterScope scope) {
        return super.attachCluster(store, scope);
    }

    @Override
    public boolean attachHost(DataStore store, HostScope scope, StoragePoolInfo existingInfo) {
        return super.attachHost(store, scope, existingInfo);
    }

    @Override
    public boolean attachZone(DataStore dataStore, ZoneScope scope, HypervisorType hypervisor) {
        return super.attachZone(dataStore,scope, hypervisor);
    }

    @Override
    public boolean maintain(DataStore store) {
        return super.maintain(store);
    }

    @Override
    public boolean cancelMaintain(DataStore store) {
        return super.cancelMaintain(store);
    }

    @Override
    public boolean deleteDataStore(DataStore store) {
        return super.deleteDataStore(store);
    }

    /* (non-Javadoc)
     * @see org.apache.cloudstack.engine.subsystem.api.storage.DataStoreLifeCycle#migrateToObjectStore(org.apache.cloudstack.engine.subsystem.api.storage.DataStore)
     */
    @Override
    public boolean migrateToObjectStore(DataStore store) {
        return super.migrateToObjectStore(store);
    }

}
