package org.apache.cloudstack.storage.datastore.to;

import com.cloud.agent.api.LogLevel;
import com.cloud.agent.api.to.DataStoreTO;
import com.cloud.storage.DataStoreRole;

import java.util.Date;

public class FreeNASTO implements  DataStoreTO {



    private Long id;
    private String uuid;


    @LogLevel(LogLevel.Log4jLevel.Off)
    private String userName;
    @LogLevel(LogLevel.Log4jLevel.Off)
    private String password;

    private String endPoint;

    private String protocol;

    private Boolean httpsFlag;
    private Date created;




    private static final String pathSeparator = "/";

    @Override
    public DataStoreRole getRole() {
        return DataStoreRole.Image;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public String getUrl() {
        return getEndPoint();
    }

    @Override
    public String getPathSeparator() {
        return pathSeparator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Boolean getHttpsFlag() {
        return httpsFlag;
    }

    public void setHttpsFlag(Boolean httpsFlag) {
        this.httpsFlag = httpsFlag;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
