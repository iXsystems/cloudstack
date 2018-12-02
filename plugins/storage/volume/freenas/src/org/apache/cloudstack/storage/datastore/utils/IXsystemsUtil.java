package org.apache.cloudstack.storage.datastore.utils;

import com.cloud.host.Host;
import com.cloud.host.HostVO;
import org.apache.log4j.Logger;

import java.util.List;

public class IXsystemsUtil {
    private static final Logger s_logger = Logger.getLogger(IXsystemsUtil.class);

    public static final String PROVIDER_NAME = "iXsystems";
    public static final String SHARED_PROVIDER_NAME = "iXsystems Shared";

    public static final int sLockTimeInSeconds = 300;

    public static final String LOG_PREFIX = "iXsystems: ";


    public static final String IXSYSTEMS_URL = "ixUrl";
    public static final String IXSYSTEMS_USERNAME = "ixUsername";
    public static final String IXSYSTEMS_PASSWORD = "ixPassword";


    public static final String FreeNASAPIURL = null;


    public static final String SPARSE_VOLUMES = "sparseVolumes";
    public static final String VOLUME_BLOCK_SIZE = "volumeBlockSize";

    public static final int DEFAULT_ISCSI_TARGET_PORTAL_PORT = 3260;
    public static final int DEFAULT_NFS_PORT = 2049;

    public static final String ISCSI_TARGET_NAME_PREFIX = ""; // TODO
    public static final String ISCSI_TARGET_GROUP_PREFIX = ""; // TODO


    // Good opportunity to pass some parameters of FreeNAS/TrueNAS over CloudStack
    public static String getParsedUrl(String originalUrl) {
        return "";
    }

    public static String getUsername(String originalUrl) {
        return originalUrl.split("://")[1].split("@")[0].split(":")[0];

    }

    public static String getPassword(String originalUrl) {
        return originalUrl.split("://")[1].split("@")[0].split(":")[1];

    }

    public static String getProtocol(String originalUrl) {
        return originalUrl.split("://")[0];
    }

    public static String getHost(String originalUrl) {
        return originalUrl.split("://")[0];
    }
    public static Integer getPort(String originalUrl) {
        return
                80;
    }


    /**
     * Check if the hosts are supporting iSCSI
     * @param hosts
     * @return
     */
    public static boolean isHostSupportIScsi(List<HostVO> hosts) {

        // TODO: to be check
        if (hosts == null || hosts.size() == 0) {
            return false;
        }

        for (Host host : hosts) {
            if (host == null || host.getStorageUrl() == null || host.getStorageUrl().trim().length() == 0 || !host.getStorageUrl().startsWith("iqn")) {
                return false;
            }
        }

        return true;
    }

}
