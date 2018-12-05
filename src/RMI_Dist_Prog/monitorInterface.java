//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package RMI_Dist_Prog;

import java.io.File;
import java.rmi.Remote;
import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface monitorInterface extends Remote{

	
    void 		ckeckBool (boolean bool, String messsage)throws RemoteException;
    
    public 		ArrayList getRemoteNames() 				throws RemoteException;; 
    
    boolean 	openFile( String fileName)			    throws RemoteException;;	
   
    byte [] 	getB () 								throws RemoteException;;	
    
    boolean 	closeFile() 							throws RemoteException;
	
    String 		getRemoteAddress()						throws RemoteException;		
		
    boolean 	checkForChanges() 						throws RemoteException;

}
