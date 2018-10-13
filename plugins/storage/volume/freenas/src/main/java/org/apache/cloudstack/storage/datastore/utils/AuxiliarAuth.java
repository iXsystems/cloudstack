package org.apache.cloudstack.storage.datastore.utils;

import org.freenas.client.connectors.rest.imp.AuthenticationConnector;

public class AuxiliarAuth {


    public static AuthenticationConnector getAuth(){
        AuthenticationConnector auth = new AuthenticationConnector("root", "abcd");
        return auth;
    }

}
