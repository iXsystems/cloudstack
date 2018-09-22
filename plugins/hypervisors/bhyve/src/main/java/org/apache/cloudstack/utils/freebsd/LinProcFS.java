package org.apache.cloudstack.utils.freebsd;


import com.cloud.utils.script.Script;

public class LinProcFS {

    /**
     * Check in FreeBSD: man 5 linprocfs
     * @return
     */
    private boolean mountLinProcFS(){
        //mount -t linprocfs proc /proc
        String mountLinProcFSResult = Script.runSimpleBashScript("mount -t linprocfs proc /proc");

        return false;
    }

    /**
     * This method is to check if linprocfs is currectly mounted or not.
     * @return
     */
    private boolean isLinProcFSMounted(){
        return false;
    }

}
