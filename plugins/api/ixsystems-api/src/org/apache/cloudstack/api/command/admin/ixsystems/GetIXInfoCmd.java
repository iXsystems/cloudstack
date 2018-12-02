package org.apache.cloudstack.api.command.admin.ixsystems;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.response.ixsystems.ApiIXVolumesResponse;
import org.apache.log4j.Logger;



@APICommand(name = "get",
        responseObject = ApiIXVolumesResponse.class,
        description = "Get the available volumes",
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class GetIXInfoCmd extends BaseCmd {
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
