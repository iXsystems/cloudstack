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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.cloud.agent.api.to.DataObjectType;
import com.cloud.dc.dao.ClusterDao;
import com.cloud.host.dao.HostDao;


import com.cloud.storage.dao.SnapshotDao;
import com.cloud.storage.dao.SnapshotDetailsDao;
import com.cloud.storage.dao.StoragePoolHostDao;
import com.cloud.storage.dao.VolumeDetailsDao;
import com.cloud.storage.dao.VolumeDao;

import com.cloud.storage.dao.VMTemplatePoolDao;
import com.cloud.user.AccountDetailsDao;
import com.cloud.user.dao.AccountDao;

import org.apache.cloudstack.engine.subsystem.api.storage.CreateCmdResult;
import org.apache.cloudstack.engine.subsystem.api.storage.EndPoint;
import org.apache.cloudstack.engine.subsystem.api.storage.ChapInfo;
import org.apache.cloudstack.engine.subsystem.api.storage.CopyCommandResult;
import org.apache.cloudstack.engine.subsystem.api.storage.PrimaryDataStoreDriver;
import org.apache.cloudstack.engine.subsystem.api.storage.EndPointSelector;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreManager;
import org.apache.cloudstack.engine.subsystem.api.storage.VolumeDataFactory;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreCapabilities;
import org.apache.cloudstack.engine.subsystem.api.storage.DataObject;
import org.apache.cloudstack.engine.subsystem.api.storage.VolumeInfo;

import org.apache.cloudstack.engine.subsystem.api.storage.DataStore;
import org.apache.cloudstack.engine.subsystem.api.storage.SnapshotInfo;
import org.apache.cloudstack.engine.subsystem.api.storage.TemplateInfo;

import org.apache.cloudstack.storage.datastore.db.PrimaryDataStoreDao;
import org.apache.cloudstack.storage.datastore.db.StoragePoolDetailsDao;
import org.apache.log4j.Logger;

import org.apache.cloudstack.framework.async.AsyncCallbackDispatcher;
import org.apache.cloudstack.framework.async.AsyncCompletionCallback;
import org.apache.cloudstack.framework.async.AsyncRpcContext;
import org.apache.cloudstack.storage.command.CommandResult;
import org.apache.cloudstack.storage.command.CreateObjectCommand;
import org.apache.cloudstack.storage.datastore.DataObjectManager;


import com.cloud.agent.api.Answer;
import com.cloud.agent.api.to.DataStoreTO;
import com.cloud.agent.api.to.DataTO;
import com.cloud.host.Host;
import com.cloud.storage.StoragePool;
import com.cloud.utils.exception.CloudRuntimeException;


public class FreeNASPrimaryDataStoreDriverImpl implements PrimaryDataStoreDriver {
    private static final Logger s_logger = Logger.getLogger(FreeNASPrimaryDataStoreDriverImpl.class);
    @Inject
    EndPointSelector selector;
    @Inject
    StoragePoolHostDao storeHostDao;
    @Inject
    DataObjectManager dataObjMgr;

    @Inject private AccountDao accountDao;
    @Inject private AccountDetailsDao accountDetailsDao;
    @Inject private ClusterDao clusterDao;
    @Inject private DataStoreManager dataStoreMgr;
    @Inject private HostDao hostDao;
    @Inject private SnapshotDao snapshotDao;
    @Inject private SnapshotDetailsDao snapshotDetailsDao;
    @Inject private PrimaryDataStoreDao storagePoolDao;
    @Inject private StoragePoolDetailsDao storagePoolDetailsDao;
    @Inject private VMTemplatePoolDao tmpltPoolDao;
    @Inject private VolumeDao volumeDao;
    @Inject private VolumeDetailsDao volumeDetailsDao;
    @Inject private VolumeDataFactory volumeFactory;

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
        return null;
    }

    @Override
    public DataStoreTO getStoreTO(DataStore store) {
        return null;
    }




    @Override
    public ChapInfo getChapInfo(DataObject dataObject) {
        return null;
    }

    @Override
    public boolean grantAccess(DataObject dataObject, Host host, DataStore dataStore) { return false; }

    @Override
    public void revokeAccess(DataObject dataObject, Host host, DataStore dataStore) {}


    @Override
    public long getUsedBytes(StoragePool storagePool) {
        return 0;
    }

    @Override
    public long getUsedIops(StoragePool storagePool) {
        return 0;
    }

    @Override
    public void takeSnapshot(SnapshotInfo snapshot, AsyncCompletionCallback<CreateCmdResult> callback) {

    }

    @Override
    public void revertSnapshot(SnapshotInfo snapshotOnImageStore, SnapshotInfo snapshotOnPrimaryStore, AsyncCompletionCallback<CommandResult> callback) {

    }

    @Override
    public void handleQualityOfServiceForVolumeMigration(VolumeInfo volumeInfo, QualityOfServiceState qualityOfServiceState) {

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
    }

    @Override
    public void copyAsync(DataObject srcdata, DataObject destData,
                          AsyncCompletionCallback<CopyCommandResult> callback) {

    }

    @Override
    public boolean canCopy(DataObject srcData, DataObject destData) {
        return false;
    }

    @Override
    public void resize(DataObject data, AsyncCompletionCallback<CreateCmdResult> callback) {

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

    /*
     * private class CreateVolumeFromBaseImageContext<T> extends
     * AsyncRpcContext<T> { private final VolumeObject volume;
     *
     * public CreateVolumeFromBaseImageContext(AsyncCompletionCallback<T>
     * callback, VolumeObject volume) { super(callback); this.volume = volume; }
     *
     * public VolumeObject getVolume() { return this.volume; }
     *
     * }
     *
     * @Override public void createVolumeFromBaseImageAsync(VolumeObject volume,
     * TemplateInfo template, AsyncCompletionCallback<CommandResult> callback) {
     * VolumeTO vol = this.dataStore.getVolumeTO(volume); List<EndPoint>
     * endPoints = this.dataStore.getEndPoints(); EndPoint ep =
     * endPoints.get(0); String templateUri =
     * template.getDataStore().grantAccess(template, ep);
     * CreateVolumeFromBaseImageCommand cmd = new
     * CreateVolumeFromBaseImageCommand(vol, templateUri);
     *
     * CreateVolumeFromBaseImageContext<CommandResult> context = new
     * CreateVolumeFromBaseImageContext<CommandResult>(callback, volume);
     * AsyncCallbackDispatcher<DefaultPrimaryDataStoreDriverImpl, Answer> caller
     * = AsyncCallbackDispatcher.create(this); caller.setContext(context)
     * .setCallback
     * (caller.getTarget().createVolumeFromBaseImageAsyncCallback(null, null));
     *
     * ep.sendMessageAsync(cmd, caller); }
     */
    /*
     * public Object
     * createVolumeFromBaseImageAsyncCallback(AsyncCallbackDispatcher
     * <DefaultPrimaryDataStoreDriverImpl, Answer> callback,
     * CreateVolumeFromBaseImageContext<CommandResult> context) {
     * CreateVolumeAnswer answer = (CreateVolumeAnswer)callback.getResult();
     * CommandResult result = new CommandResult(); if (answer == null ||
     * answer.getDetails() != null) { result.setSuccess(false); if (answer !=
     * null) { result.setResult(answer.getDetails()); } } else {
     * result.setSuccess(true); VolumeObject volume = context.getVolume();
     * volume.setPath(answer.getVolumeUuid()); }
     * AsyncCompletionCallback<CommandResult> parentCall =
     * context.getParentCallback(); parentCall.complete(result); return null; }
     */

    @Override
    public void createAsync(DataStore dataStore, DataObject vol, AsyncCompletionCallback<CreateCmdResult> callback) {
        EndPoint ep = selector.select(vol);
        if (ep == null) {
            String errMsg = "No remote endpoint to send command, check if host or ssvm is down?";
            s_logger.error(errMsg);
            throw new CloudRuntimeException(errMsg);
        }
        CreateObjectCommand createCmd = new CreateObjectCommand(null);

        CreateVolumeContext<CreateCmdResult> context = new CreateVolumeContext<CreateCmdResult>(callback, vol);
        AsyncCallbackDispatcher<FreeNASPrimaryDataStoreDriverImpl, Answer> caller = AsyncCallbackDispatcher.create(this);
        caller.setContext(context).setCallback(caller.getTarget().createAsyncCallback(null, null));

        ep.sendMessageAsync(createCmd, caller);

        String iqn = null;
        String errMsg = null;
        DataObject dataObject = vol;
        try {
            if (dataObject.getType() == DataObjectType.VOLUME) {

            } else if (dataObject.getType() == DataObjectType.SNAPSHOT) {

            } else if (dataObject.getType() == DataObjectType.TEMPLATE) {

            } else {
                errMsg = "Invalid DataObjectType (" + dataObject.getType() + ") passed to createAsync";

            }
        }
        catch (Exception ex) {
            errMsg = ex.getMessage();

            if (callback == null) {
                throw ex;
            }
        }

        if (callback != null) {
            // path = iqn
            // size is pulled from DataObject instance, if errMsg is null
            CreateCmdResult result = new CreateCmdResult(iqn, new Answer(null, errMsg == null, errMsg));

            result.setResult(errMsg);

            callback.complete(result);
        }
        else {
            if (errMsg != null) {
                throw new CloudRuntimeException(errMsg);
            }
        }


    }

}
