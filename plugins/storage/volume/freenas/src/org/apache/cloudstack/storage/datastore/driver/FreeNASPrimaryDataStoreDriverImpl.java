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
package org.apache.cloudstack.storage.datastore.driver;

import com.cloud.agent.api.Answer;
import com.cloud.agent.api.to.DataStoreTO;
import com.cloud.agent.api.to.DataTO;
import com.cloud.dc.dao.ClusterDao;
import com.cloud.host.Host;
import com.cloud.host.dao.HostDao;
import com.cloud.storage.Storage;
import com.cloud.storage.StoragePool;
import com.cloud.storage.VolumeVO;
import com.cloud.storage.dao.SnapshotDao;
import com.cloud.storage.dao.SnapshotDetailsDao;
import com.cloud.storage.dao.StoragePoolHostDao;
import com.cloud.storage.dao.VMTemplatePoolDao;
import com.cloud.storage.dao.VolumeDao;
import com.cloud.storage.dao.VolumeDetailsDao;
import com.cloud.user.AccountDetailsDao;
import com.cloud.user.dao.AccountDao;
import com.ixsystems.vcp.entities.Dataset;
import org.apache.cloudstack.engine.subsystem.api.storage.ChapInfo;
import org.apache.cloudstack.engine.subsystem.api.storage.CopyCommandResult;
import org.apache.cloudstack.engine.subsystem.api.storage.CreateCmdResult;
import org.apache.cloudstack.engine.subsystem.api.storage.DataObject;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStore;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreCapabilities;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreManager;
import org.apache.cloudstack.engine.subsystem.api.storage.EndPointSelector;
import org.apache.cloudstack.engine.subsystem.api.storage.PrimaryDataStoreDriver;
import org.apache.cloudstack.engine.subsystem.api.storage.SnapshotInfo;
import org.apache.cloudstack.engine.subsystem.api.storage.TemplateInfo;
import org.apache.cloudstack.engine.subsystem.api.storage.VolumeDataFactory;
import org.apache.cloudstack.engine.subsystem.api.storage.VolumeInfo;
import org.apache.cloudstack.framework.async.AsyncCallbackDispatcher;
import org.apache.cloudstack.framework.async.AsyncCompletionCallback;
import org.apache.cloudstack.framework.async.AsyncRpcContext;
import org.apache.cloudstack.storage.command.CommandResult;
import org.apache.cloudstack.storage.datastore.DataObjectManager;
import org.apache.cloudstack.storage.datastore.db.PrimaryDataStoreDao;
import org.apache.cloudstack.storage.datastore.db.StoragePoolDetailsDao;
import org.apache.cloudstack.storage.datastore.utils.AuxiliarAuth;
import org.apache.log4j.Logger;
import org.freenas.client.connectors.rest.imp.AuthenticationConnector;
import org.freenas.client.connectors.rest.imp.EndpointConnector;
import org.freenas.client.storage.rest.impl.DatasetRestConnector;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 *
 */
public class FreeNASPrimaryDataStoreDriverImpl extends CloudStackPrimaryDataStoreDriverImpl implements PrimaryDataStoreDriver  {
    private static final Logger s_logger = Logger.getLogger(FreeNASPrimaryDataStoreDriverImpl.class);
    @Inject
    EndPointSelector selector;
    @Inject
    StoragePoolHostDao storeHostDao;
    @Inject
    DataObjectManager dataObjMgr;

    @Inject
    private AccountDao accountDao;
    @Inject
    private AccountDetailsDao accountDetailsDao;
    @Inject
    private ClusterDao clusterDao;
    @Inject
    private DataStoreManager dataStoreMgr;
    @Inject
    private HostDao hostDao;
    @Inject
    private SnapshotDao snapshotDao;
    @Inject
    private SnapshotDetailsDao snapshotDetailsDao;
    @Inject
    private PrimaryDataStoreDao storagePoolDao;
    @Inject
    private StoragePoolDetailsDao storagePoolDetailsDao;
    @Inject
    private VMTemplatePoolDao tmpltPoolDao;
    @Inject
    private VolumeDao volumeDao;
    @Inject
    private VolumeDetailsDao volumeDetailsDao;
    @Inject
    private VolumeDataFactory volumeFactory;

    public FreeNASPrimaryDataStoreDriverImpl() {

    }

    @Override
    public Map<String, String> getCapabilities() {
        Map<String, String> mapCapabilities = new HashMap<>();

        mapCapabilities.put(DataStoreCapabilities.STORAGE_SYSTEM_SNAPSHOT.toString(), Boolean.TRUE.toString());
        mapCapabilities.put(DataStoreCapabilities.CAN_CREATE_VOLUME_FROM_SNAPSHOT.toString(), Boolean.TRUE.toString());
        mapCapabilities.put(DataStoreCapabilities.CAN_CREATE_VOLUME_FROM_VOLUME.toString(), Boolean.TRUE.toString());
        mapCapabilities.put(DataStoreCapabilities.CAN_REVERT_VOLUME_TO_SNAPSHOT.toString(), Boolean.TRUE.toString());

        return mapCapabilities;
    }

    @Override
    public DataTO getTO(DataObject data) {
        return super.getTO(data);
    }

    @Override
    public DataStoreTO getStoreTO(DataStore store) {
        return super.getStoreTO(store);
    }


    @Override
    public ChapInfo getChapInfo(DataObject dataObject) {
        return super.getChapInfo(dataObject);
    }

    @Override
    public boolean grantAccess(DataObject dataObject, Host host, DataStore dataStore) {
        return super.grantAccess(dataObject, host, dataStore);
    }

    @Override
    public void revokeAccess(DataObject dataObject, Host host, DataStore dataStore) {
        super.revokeAccess(dataObject, host, dataStore);
    }


    @Override
    public long getUsedBytes(StoragePool storagePool) {
        return super.getUsedBytes(storagePool);
    }

    @Override
    public long getUsedIops(StoragePool storagePool) {
        return super.getUsedIops(storagePool);
    }

    @Override
    public void takeSnapshot(SnapshotInfo snapshot,
                             AsyncCompletionCallback<CreateCmdResult> callback) {
        super.takeSnapshot(snapshot, callback);
    }

    @Override
    public void revertSnapshot(SnapshotInfo snapshotOnImageStore,
                               SnapshotInfo snapshotOnPrimaryStore,
                               AsyncCompletionCallback<CommandResult> callback) {
        super.revertSnapshot(snapshotOnImageStore, snapshotOnPrimaryStore, callback);
    }

    @Override
    public long getDataObjectSizeIncludingHypervisorSnapshotReserve(DataObject dataObject, StoragePool pool) {
        return dataObject.getSize();
    }

    @Override
    public long getBytesRequiredForTemplate(TemplateInfo templateInfo, StoragePool storagePool) {
        return 0;
    }

    private class CreateVolumeContext<T> extends AsyncRpcContext<T> {
        public CreateVolumeContext(AsyncCompletionCallback<T> callback, DataObject volume) {
            super(callback);
        }
    }

    public Void createAsyncCallback(AsyncCallbackDispatcher<FreeNASPrimaryDataStoreDriverImpl, Answer> callback, CreateVolumeContext<CreateCmdResult> context) {
        /*
         * CreateCmdResult result = null; CreateObjectAnswer volAnswer =
         * (CreateObjectAnswer) callback.getResult(); if (volAnswer.getResult())
         * { result = new CreateCmdResult(volAnswer.getPath(), volAnswer); }
         * else { result = new CreateCmdResult("", null);
         * result.setResult(volAnswer.getDetails()); }
         *
         * context.getParentCallback().complete(result);
         */
        return null;
    }

    @Override
    public void deleteAsync(DataStore dataStore, DataObject vo, AsyncCompletionCallback<CommandResult> callback) {
        /*
         * DeleteCommand cmd = new DeleteCommand(vo.getUri());
         *
         * EndPoint ep = selector.select(vo); AsyncRpcContext<CommandResult>
         * context = new AsyncRpcContext<CommandResult>(callback);
         * AsyncCallbackDispatcher<FreeNASPrimaryDataStoreDriverImpl, Answer>
         * caller = AsyncCallbackDispatcher.create(this);
         * caller.setCallback(caller.getTarget().deleteCallback(null, null))
         * .setContext(context); ep.sendMessageAsync(cmd, caller);
         */
        super.deleteAsync(dataStore, vo, callback);
    }

    @Override
    public void copyAsync(DataObject srcdata, DataObject destData,
                          AsyncCompletionCallback<CopyCommandResult> callback) {

        super.copyAsync(srcdata, destData, callback);

    }

    @Override
    public boolean canCopy(DataObject srcData, DataObject destData) {
        return super.canCopy(srcData, destData);
    }

    @Override
    public void resize(DataObject data, AsyncCompletionCallback<CreateCmdResult> callback) {
        super.resize(data, callback);
    }

    public Void deleteCallback(AsyncCallbackDispatcher<FreeNASPrimaryDataStoreDriverImpl, Answer> callback, AsyncRpcContext<CommandResult> context) {
        CommandResult result = new CommandResult();
        Answer answer = callback.getResult();
        if (!answer.getResult()) {
            result.setResult(answer.getDetails());
        }
        context.getParentCallback().complete(result);
        return null;
    }


    /**
     * This method allows to create any storage object;
     * Several type of objects
     *
     * @param dataStore
     * @param vol
     * @param callback
     */
    @Override
    public void createAsync(DataStore dataStore, DataObject vol,
                            AsyncCompletionCallback<CreateCmdResult> callback) {
        super.createAsync(dataStore, vol, callback);
        /*
        String iqn = null;
        String errMsg = null;
        DataObject dataObject = vol;
        try {
            if (dataObject.getType() == DataObjectType.VOLUME) {
                iqn = createVolume((VolumeInfo) dataObject, dataStore.getId());
            } else if (dataObject.getType() == DataObjectType.SNAPSHOT) {
                createTempVolume((SnapshotInfo) dataObject, dataStore.getId());

            } else if (dataObject.getType() == DataObjectType.TEMPLATE) {
                iqn = createTemplateVolume((TemplateInfo) dataObject, dataStore.getId());

            } else {
                errMsg = "Invalid DataObjectType (" + dataObject.getType() + ") passed to createAsync";

            }
        } catch (Exception ex) {
            errMsg = ex.getMessage();

            if (callback == null) {
                throw ex;
            }
        }

        if (callback != null) {

            CreateCmdResult result = new CreateCmdResult(iqn, new Answer(null, errMsg == null, errMsg));
            result.setResult(errMsg);
            callback.complete(result);
        } else {
            if (errMsg != null) {
                throw new CloudRuntimeException(errMsg);
            }
        }
        */


    }


    private DatasetRestConnector getConnector(){
        AuthenticationConnector auth = AuxiliarAuth.getAuth();
        EndpointConnector ep = new EndpointConnector("http://10.20.21.194", "http");
        DatasetRestConnector gs = new DatasetRestConnector(ep, auth);

        return gs;
    }

    /**
     * Create Volume
     *
     * @param volumeInfo
     * @param storagePoolId
     * @return
     */
    private String createVolume(VolumeInfo volumeInfo, long storagePoolId) {

        DatasetRestConnector gs = getConnector();
        Map<String, String> args = new HashMap<String, String>();

        args.put("name", "datasetNameTest"+ UUID.randomUUID().toString());

        String volumeName = "zz";


        Dataset ds  = gs.create(volumeName, args);


        VolumeVO volume = volumeDao.findById(volumeInfo.getId());

        volume.setPoolType(Storage.StoragePoolType.NetworkFilesystem);
        volume.setPoolId(storagePoolId);


        return "";
    }

    private String createIscsiVolume(String volumeName, Long volumeSize) {
        return "";
}


    /**
     * Create temporary volume - snapshot
     * @param snapshotInfo
     * @param storagePoolId
     */
    private void createTempVolume(SnapshotInfo snapshotInfo, long storagePoolId) {

    }


    /**
     * Create Template Volume
     *
     * @param templateInfo
     * @param storagePoolId
     * @return
     */
    private String createTemplateVolume(TemplateInfo templateInfo, long storagePoolId) {
        return "";
    }


}
