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

package org.apache.cloudstack.bhyve.ha;

import com.cloud.host.Host;
import com.cloud.hypervisor.Hypervisor;

import org.apache.cloudstack.api.response.OutOfBandManagementResponse;
import org.apache.cloudstack.framework.config.ConfigKey;
import org.apache.cloudstack.framework.config.Configurable;
import org.apache.cloudstack.ha.HAResource;
import org.apache.cloudstack.ha.provider.HACheckerException;
import org.apache.cloudstack.ha.provider.HAFenceException;
import org.apache.cloudstack.ha.provider.HAProvider;
import org.apache.cloudstack.ha.provider.HARecoveryException;
import org.apache.cloudstack.ha.provider.host.HAAbstractHostProvider;
import org.apache.cloudstack.outofbandmanagement.OutOfBandManagement.PowerOperation;
import org.apache.cloudstack.outofbandmanagement.OutOfBandManagementService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.security.InvalidParameterException;

public final class BHyveHAProvider extends HAAbstractHostProvider implements HAProvider<Host>, Configurable {
    private final static Logger LOG = Logger.getLogger(BHyveHAProvider.class);

    @Inject
    protected BHyveHostActivityChecker hostActivityChecker;
    @Inject
    protected OutOfBandManagementService outOfBandManagementService;

    @Override
    public boolean isEligible(final Host host) {
       if (outOfBandManagementService.isOutOfBandManagementEnabled(host)){
            return !isInMaintenanceMode(host) && !isDisabled(host) &&
                    hostActivityChecker.getNeighbors(host).length > 0 &&
                    (Hypervisor.HypervisorType.KVM.equals(host.getHypervisorType()) ||
                            Hypervisor.HypervisorType.LXC.equals(host.getHypervisorType()));
        }
        return false;
    }

    @Override
    public boolean isHealthy(final Host r) throws HACheckerException {
        return hostActivityChecker.isHealthy(r);
    }

    @Override
    public boolean hasActivity(final Host r, final DateTime suspectTime) throws HACheckerException {
        return hostActivityChecker.isActive(r, suspectTime);
    }

    @Override
    public boolean recover(Host r) throws HARecoveryException {
        try {
            if (outOfBandManagementService.isOutOfBandManagementEnabled(r)){
                final OutOfBandManagementResponse resp = outOfBandManagementService.executePowerOperation(r, PowerOperation.RESET, null);
                return resp.getSuccess();
            } else {
                LOG.warn("OOBM recover operation failed for the host " + r.getName());
                return false;
            }
        } catch (Exception e){
            LOG.warn("OOBM service is not configured or enabled for this host " + r.getName() + " error is " + e.getMessage());
            throw new HARecoveryException(" OOBM service is not configured or enabled for this host " + r.getName(), e);
        }
    }

    @Override
    public boolean fence(Host r) throws HAFenceException {
        try {
            if (outOfBandManagementService.isOutOfBandManagementEnabled(r)){
                final OutOfBandManagementResponse resp = outOfBandManagementService.executePowerOperation(r, PowerOperation.OFF, null);
                return resp.getSuccess();
            } else {
                LOG.warn("OOBM fence operation failed for this host " + r.getName());
                return false;
            }
        } catch (Exception e){
            LOG.warn("OOBM service is not configured or enabled for this host " + r.getName() + " error is " + e.getMessage());
            throw new HAFenceException("OOBM service is not configured or enabled for this host " + r.getName() , e);
        }
    }

    @Override
    public HAResource.ResourceSubType resourceSubType() {
        return HAResource.ResourceSubType.KVM;
    }

    @Override
    public Object getConfigValue(final HAProviderConfig name, final Host host) {
        final Long clusterId = host.getClusterId();
        switch (name) {
            case HealthCheckTimeout:
                return BHyveHAConfig.BhyveHAHealthCheckTimeout.valueIn(clusterId);
            case ActivityCheckTimeout:
                return BHyveHAConfig.BhyveHAActivityCheckTimeout.valueIn(clusterId);
            case MaxActivityCheckInterval:
                return BHyveHAConfig.BhyveHAActivityCheckInterval.valueIn(clusterId);
            case MaxActivityChecks:
                return BHyveHAConfig.BhyveHAActivityCheckMaxAttempts.valueIn(clusterId);
            case ActivityCheckFailureRatio:
                return BHyveHAConfig.BhyveHAActivityCheckFailureThreshold.valueIn(clusterId);
            case RecoveryWaitTimeout:
                return BHyveHAConfig.BhyveHARecoverWaitPeriod.valueIn(clusterId);
            case RecoveryTimeout:
                return BHyveHAConfig.BhyveHARecoverTimeout.valueIn(clusterId);
            case FenceTimeout:
                return BHyveHAConfig.BhyveHAFenceTimeout.valueIn(clusterId);
            case MaxRecoveryAttempts:
                return BHyveHAConfig.BhyveHARecoverAttemptThreshold.valueIn(clusterId);
            case MaxDegradedWaitTimeout:
                return BHyveHAConfig.BhyveHADegradedMaxPeriod.valueIn(clusterId);
            default:
                throw new InvalidParameterException("Unknown HAProviderConfig " + name.toString());
        }
    }

    @Override
    public String getConfigComponentName() {
        return BHyveHAConfig.class.getSimpleName();
    }

    @Override
    public ConfigKey<?>[] getConfigKeys() {
        return new ConfigKey<?>[] {
            BHyveHAConfig.BhyveHAHealthCheckTimeout,
            BHyveHAConfig.BhyveHAActivityCheckTimeout,
            BHyveHAConfig.BhyveHARecoverTimeout,
            BHyveHAConfig.BhyveHAFenceTimeout,
            BHyveHAConfig.BhyveHAActivityCheckInterval,
            BHyveHAConfig.BhyveHAActivityCheckMaxAttempts,
            BHyveHAConfig.BhyveHAActivityCheckFailureThreshold,
            BHyveHAConfig.BhyveHADegradedMaxPeriod,
            BHyveHAConfig.BhyveHARecoverWaitPeriod,
            BHyveHAConfig.BhyveHARecoverAttemptThreshold
        };
    }
}
