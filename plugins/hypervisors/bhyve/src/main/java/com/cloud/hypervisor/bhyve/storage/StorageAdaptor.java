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
package com.cloud.hypervisor.bhyve.storage;

import java.util.List;
import java.util.Map;

import org.apache.cloudstack.utils.qemu.QemuImg.PhysicalDiskFormat;

import com.cloud.storage.Storage;
import com.cloud.storage.Storage.StoragePoolType;

public interface StorageAdaptor {

    public BhyveStoragePool getStoragePool(String uuid);

    // Get the storage pool from libvirt, but control if libvirt should refresh the pool (can take a long time)
    public BhyveStoragePool getStoragePool(String uuid, boolean refreshInfo);

    // given disk path (per database) and pool, create new BhyvePhysicalDisk, populate
    // it with info from local disk, and return it
    public BhyvePhysicalDisk getPhysicalDisk(String volumeUuid, BhyveStoragePool pool);

    public BhyveStoragePool createStoragePool(String name, String host, int port, String path, String userInfo, StoragePoolType type);

    public boolean deleteStoragePool(String uuid);

    public BhyvePhysicalDisk createPhysicalDisk(String name, BhyveStoragePool pool,
                                                PhysicalDiskFormat format, Storage.ProvisioningType provisioningType, long size);

    // given disk path (per database) and pool, prepare disk on host
    public boolean connectPhysicalDisk(String volumePath, BhyveStoragePool pool, Map<String, String> details);

    // given disk path (per database) and pool, clean up disk on host
    public boolean disconnectPhysicalDisk(String volumePath, BhyveStoragePool pool);

    public boolean disconnectPhysicalDisk(Map<String, String> volumeToDisconnect);

    // given local path to file/device (per Libvirt XML), 1) check that device is
    // handled by your adaptor, return false if not. 2) clean up device, return true
    public boolean disconnectPhysicalDiskByPath(String localPath);

    public boolean deletePhysicalDisk(String uuid, BhyveStoragePool pool, Storage.ImageFormat format);

    public BhyvePhysicalDisk createDiskFromTemplate(BhyvePhysicalDisk template,
                                                    String name, PhysicalDiskFormat format, Storage.ProvisioningType provisioningType, long size,
                                                    BhyveStoragePool destPool, int timeout);

    public BhyvePhysicalDisk createTemplateFromDisk(BhyvePhysicalDisk disk, String name, PhysicalDiskFormat format, long size, BhyveStoragePool destPool);

    public List<BhyvePhysicalDisk> listPhysicalDisks(String storagePoolUuid, BhyveStoragePool pool);

    public BhyvePhysicalDisk copyPhysicalDisk(BhyvePhysicalDisk disk, String name, BhyveStoragePool destPools, int timeout);

    public BhyvePhysicalDisk createDiskFromSnapshot(BhyvePhysicalDisk snapshot, String snapshotName, String name, BhyveStoragePool destPool);

    public boolean refresh(BhyveStoragePool pool);

    public boolean deleteStoragePool(BhyveStoragePool pool);

    public boolean createFolder(String uuid, String path);
}
