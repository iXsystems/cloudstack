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
package org.apache.cloudstack.api.command.admin.ixsystems;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.response.ixsystems.ApiIXVolumesResponse;
import org.apache.log4j.Logger;


@APICommand(name = "getVolumes",
        responseObject = ApiIXVolumesResponse.class,
        description = "Get the available volumes",
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class GetIXVolumesCmd extends BaseCmd {
    private static final Logger LOGGER = Logger.getLogger(GetIXVolumesCmd.class.getName());
    private static final String NAME = "getvolumesresponse";

    @Parameter(name = ApiConstants.VOLUME_ID,
            type = CommandType.STRING,
            description = "CloudStack Volume UUID", required = true)
    private String _volumeUuid;


    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return NAME;
    }

    @Override
    public long getEntityOwnerId() {
        return 1L;
    }

    @Override
    public void execute() {
        LOGGER.info("'GetPathForVolumeIdCmd.execute' method invoked");

        ApiIXVolumesResponse response = new ApiIXVolumesResponse("path");

        response.setResponseName(getCommandName());
        response.setObjectName("apipathforvolume");

        setResponseObject(response);

    }
}
