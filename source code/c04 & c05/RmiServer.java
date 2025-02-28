package jmsclientservermi;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class RmiServer
{
  public static void main(String args[])
  {
    try {
    	RmiServerObj server = new RmiServerObj();
    	Registry registry = LocateRegistry.createRegistry(1099);
    	registry.rebind("rmi_server", server);

        System.out.println("Server waiting.....");
    } catch (RemoteException re) {
         System.out.println("Remote exception: " + re.toString());
    }
  }
}