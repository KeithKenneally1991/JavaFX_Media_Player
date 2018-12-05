//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package RMI_Dist_Prog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.google.common.collect.Lists;
public class monitorFolder extends UnicastRemoteObject implements monitorInterface {

	static int  fileCountRemote = new File("C:\\WorkSpace\\MediaPlayerA01\\src\\RMI_Dist_Prog\\remoteVids").list().length;
	boolean     changeOccurred = false;

	ArrayList<String>  remoteArr = new ArrayList();
	private boolean    stopRequested = false;
	private String     remoteAddress = "C:\\WorkSpace\\MediaPlayerA01\\src\\RMI_Dist_Prog\\remoteVids";


	private monitorFolder() throws RemoteException{
		super();		
			
	}
	
	public void setBoolean(boolean val)throws RemoteException{
		this.changeOccurred = val;
	}
	
	    	
	@Override
	public void ckeckBool(boolean bool, String message) throws RemoteException{ // used in many scenarios. if a boolean given as a parameter is false
														 // then display an error box. if true, display confirmation box
															// the message is a unique message displayed in the confirmation box 
		if( bool == true){
			ConfirmBox.display(message);
		}
		else{
			FailedBox.display();
		}	
	}
    
	@Override
	public ArrayList getRemoteNames() throws RemoteException{ // get file names stored inside remote server and return them in an arraylist
				
				File folder = new File(remoteAddress); // user wants to get a list of remote files
				File[] listOfFiles = folder.listFiles();
				
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
				
				remoteArr.add(i, listOfFiles[i].getName());
				
				} 
				}
			
				return remoteArr;
}
	FileInputStream inStream = null;
	 ByteArrayOutputStream bos = new ByteArrayOutputStream();

	@Override
	public boolean openFile( String fileName) throws RemoteException{ 
		
		File afile = new File(this.remoteAddress +"\\"+  fileName);
 	
	    try {
			inStream = new FileInputStream(afile);
			return true;
	    
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	    
	}

	@Override
	public byte[] getB() throws RemoteException{
		 
	        byte[] buf = new byte[1024];
	        try {
	            for (int readNum; (readNum = inStream.read(buf)) != -1;) {
	                bos.write(buf, 0, readNum); 
	                System.out.println("read " + readNum + " bytes,");
	            }
	        } catch (IOException ex) {
	           ex.printStackTrace();
	        }
	        byte[] bytes = bos.toByteArray();
			
	 	    return bytes;
 	    
	}

	@Override
	public boolean closeFile() throws RemoteException{
		try {
			
			inStream.close();
			bos.reset();
			bos.flush();
			bos.close();
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public String getRemoteAddress(){
		return this.remoteAddress;
	}
	public boolean checkForChanges()throws RemoteException{
		
		System.out.print(".");
		int compareFileCountRemote = new File("C:\\WorkSpace\\MediaPlayerA01\\src\\RMI_Dist_Prog\\remoteVids").list().length;		
		
		if (fileCountRemote != compareFileCountRemote){ // IF THE INITIAL FILE COUNT IS DIFFERENT THAN THIS NEW COUNT, CHANGE THE BOOLEAN VALUE TO TRUE
			
			fileCountRemote = compareFileCountRemote;
			
			System.out.println("Change occured");
			return true;
		}
		else{
			//System.out.println("no Change occured");
				return false;
		}
		
}
	public static void main(String [] args){
        String nom = "//Localehost//MyServer";
		
		try{
			Registry reg = LocateRegistry.createRegistry(4000);
			monitorInterface server = new monitorFolder();
			reg.rebind(nom, server);
			System.out.println("OK");
			
		}catch (Exception e) {
			// TODO: handle exception

		}
		System.out.println("Server running");
		
	}
	
	
}
