package compute;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import server.AckType;

public interface ServerInterface extends Remote 
{
    public String KeyValue(UUID messageId, String functionality,String key,String value) throws RemoteException;
    public void ackMe(UUID messageId, int callBackServer, AckType type) throws RemoteException;
    public void go(UUID messageId,  int callBackServer) throws RemoteException;
    public void prepareKeyValue(UUID messageId, String functionality,String key,String value, int callBackServer) throws RemoteException;
    public void setServersInfo(int[] OtherServersPorts, int yourPorts ) throws RemoteException;
    public int getPort() throws RemoteException;
}
