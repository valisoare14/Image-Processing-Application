package jmsclientservermi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServerObjInterface extends Remote{
	byte[] zoom(byte[] portion, double zoomPercentage, String zoomOption) throws RemoteException;
}
