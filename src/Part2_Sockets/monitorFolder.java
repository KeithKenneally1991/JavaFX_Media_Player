//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package Part2_Sockets;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import java.util.Observable;
//import com.google.common.collect.Lists;
public class monitorFolder extends Observable implements monitorInterface {
	
	
	private String remoteAddress = "C:\\WorkSpace\\MediaPlayerA01\\src\\Part2_Sockets\\remoteVids";
	static int fileCountRemote = new File("C:\\WorkSpace\\MediaPlayerA01\\src\\Part2_Sockets\\remoteVids").list().length;
	boolean changeOccurred = false;

	private boolean stopRequested = false;
	private static monitorFolder instance = new monitorFolder();
	final ExecutorService service = Executors.newCachedThreadPool();

    protected monitorFolder(){
		service.submit(new Runnable() {
			@Override
			public void run(){
				while(!stopRequested){

					try {
						
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				}
			}
		}); 
			
	}
    
	public boolean checkForChange(){
				
			int compareFileCountRemote = new File("C:\\WorkSpace\\MediaPlayerA01\\src\\Part2_Sockets\\remoteVids").list().length;		
			
			if (fileCountRemote != compareFileCountRemote){ // IF THE INITIAL FILE COUNT IS DIFFERENT THAN THIS NEW COUNT, CHANGE THE BOOLEAN VALUE TO TRUE
				
				fileCountRemote = compareFileCountRemote;
				
				System.out.println("Change occured");
				return true;
			}
			else{
				System.out.println("no Change occured");
					return false;
			}
			
	}
			
	public void shutdown(){
		this.stopRequested = true;
		service.shutdown();
	}
	public static monitorFolder getInstance(){		   
		   return instance;
	   }
	    	
	@Override
	public void ckeckBool(boolean bool, String message) { // used in many scenarios. if a boolean given as a parameter is false
														 // then display an error box. if true, display confirmation box															// the message is a unique message displayed in the confirmation box 
		if( bool == true){
			ConfirmBox.display(message);
		}
		else{
			FailedBox.display();
		}	
	}
    
	@Override
	public ArrayList getRemoteNames() { // get file names stored inside remote server and return them in an arraylist
		       ArrayList<String> remoteArr = new ArrayList();

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
	public boolean openFile( String fileName) { 
		
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
	public byte[] getB() {
		 
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
		
		public boolean closeFile2(byte [] b, String address, String name) {
						
			try {
				// THIS IS FOR DOWNLOADING A FILE
				File f = new File(this.remoteAddress+ "\\"+name);
				FileOutputStream outStream = new FileOutputStream(f);
				
				outStream.write(b);
				  				
			    return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
	public boolean getBoolean() {
		return this.changeOccurred;
	}

	public String getRemoteAddress(){
		return this.remoteAddress;
	}
	
	  static int i = 0;

	  public static void main(String args[]) throws IOException{
			
		  monitorFolder monitor  = new monitorFolder();
		  new Thread( () -> {
		      try {
		    	  DataInputStream inputFromClient = null;
		    	  ObjectOutputStream objectOutput = null;
		    	  ObjectInputStream objectInput = null;
		    	  System.out.println("Attempting to create connrction inside server..");
		          ServerSocket serverSocket = new ServerSocket(8002);
				
		        while (true) {
			        Socket socket = serverSocket.accept();
					objectOutput = new ObjectOutputStream(socket.getOutputStream());
		 		 	 objectInput = new ObjectInputStream(socket.getInputStream());
			        			      			        
			        inputFromClient = new DataInputStream(
					socket.getInputStream());
					
			        int numberFromClient = -1;
		         
			        numberFromClient = inputFromClient.readInt();
		  		   
		  		  switch(numberFromClient){
		  		   case 1:
		  			   // get arraylist of file names on server
		  			 ArrayList<String> list =  new ArrayList<String>();
		  			 	  	            
		  			 list = monitor.getRemoteNames();

		                objectOutput.writeObject(list);
		  			   
		  			   break;
		  		   case 2:

		  			   String name = inputFromClient.readUTF();

		  				monitor.openFile( name );
		  				
		                objectOutput.writeObject(monitor.getB()); 
		               // monitor.closeFile();
		    			monitor.inStream.close();
		    			monitor.bos.reset();
		    			monitor.bos.flush();
		    			monitor.bos.close();
		  			   
		  			   break;
		  			   
		  		   case 3:
		  			   		  			 
		  			 try {		  		
		  				 Object object = null;

			  			   String nameBeingUploaded = inputFromClient.readUTF();

			  			   if(nameBeingUploaded != null){
			  				   	 
							object = objectInput.readObject(); // HERE !!
														
							 byte [] bytes =  (byte[]) object; // read in the byte array from server

							    monitor.closeFile2(bytes, monitor.remoteAddress, nameBeingUploaded);
							    System.out.println("File recieved");
			  			   }
		  				
																					 							
						} catch (IOException | ClassNotFoundException e) {
					    	  System.out.println("Error caught uploading");
						}
		  			   break;
		 		   

		  		 case 4:
		    	    	objectInput.close();
						
					objectOutput.close();
						 inputFromClient.close();
						 socket.close();
						
		    	    	break;
		  		   }
		         		        		   
		        }
		      }
		      catch(IOException ex) {
		        System.out.println("Error in monitor");
		    	  ex.printStackTrace();
		      }
		    }).start();	
		 
		  // CREATE  A NEW THREAD FOR THE LISTENER SOCKET CONNECTION
		  new Thread( () -> {
		  try {
				System.out.println("Attempting to create connrction inside server..");
		        try {
					ServerSocket listenerSocket = new ServerSocket(9002);
					
					listListener listAdapter = new monitorAdapter(new monitorFolder());

						        	       
		        while (true) {
			        Socket lsocket = listenerSocket.accept();
			        DataOutputStream output = new DataOutputStream(lsocket.getOutputStream());
			       
			        int checkChange = 0;
			        
			        // USING AN ADPATER CHECK IF UPDATECHANGE RETURNS TRUE
				        if(listAdapter.checkForChange() == true){
				        	checkChange = 1; 
		           }
			        else{
			        	checkChange = 0;
			        }
			       
		            output.writeInt(checkChange);
				    Thread.sleep(3000);
		        }
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		  } catch (IOException e) {
				e.printStackTrace();
			}
		    }).start();	


		 } // end main
	
	@Override
	public boolean closeFile() {  // NOT BEING USED USING A DIFFFERNET METHOD CLOSEFILE2
		// TODO Auto-generated method stub
		return false;
	}
	  
	
	
}
