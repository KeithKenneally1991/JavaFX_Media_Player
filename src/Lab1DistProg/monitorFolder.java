//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package Lab1DistProg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Observable;
//import com.google.common.collect.Lists;
public class monitorFolder extends Observable implements monitorInterface {

	//static int fileCount = new File("C:\\WorkSpace\\Semester 5\\src\\Lab1DistProg\\localVids").list().length;
	static int fileCountRemote = new File("C:\\WorkSpace\\MediaPlayerA01\\src\\Lab1DistProg\\remoteVids").list().length;
	boolean changeOccurred = false;

	 final List<listListener> listeners  = new ArrayList();
	ArrayList<String> remoteArr = new ArrayList();
	private boolean stopRequested = false;
	myMediaPlayer mp = new myMediaPlayer();
	//-----------------------------------------------------
	// implement singleton pattern
	private static monitorFolder instance = new monitorFolder();
	final ExecutorService service = Executors.newCachedThreadPool();

	//int c = 0;
	private monitorFolder(){
		
		service.submit(new Runnable(){
			@Override
			public void run(){
				while(!stopRequested){
					try{
						Thread.sleep(2000);
						
					}catch(InterruptedException e){
						
					}
					
					
					for(listListener listener : listeners){
						
						int compareFileCountRemote = new File("C:\\WorkSpace\\MediaPlayerA01\\src\\Lab1DistProg\\remoteVids").list().length;
						setBoolean(false);
						
						if (fileCountRemote != compareFileCountRemote){ // IF THE INITIAL FILE COUNT IS DIFFERENT THAN THIS NEW COUNT, CHANGE THE BOOLEAN VALUE TO TRUE
							
							setBoolean(true);
							fileCountRemote = compareFileCountRemote;
							
						}
						
						listener.checkForChanges();
					}
					
				}
			}
		});
			
	}
	
	public void addListener(listListener listener){
		listeners.add(listener );
		
		}
	
	public void setBoolean(boolean val){
		this.changeOccurred = val;
	}
	public void shutdown(){
		this.stopRequested = true;
		service.shutdown();
	}
	public static monitorFolder getInstance(){		   
		   return instance;
	   }
	    
	myMediaPlayer myMedia = new myMediaPlayer();
	
	private String remoteAddress = "C:\\WorkSpace\\MediaPlayerA01\\src\\Lab1DistProg\\remoteVids";

	@Override
	public void ckeckBool(boolean bool, String message) { // used in many scenarios. if a boolean given as a parameter is false
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
	public ArrayList getRemoteNames() { // get file names stored inside remote server and return them in an arraylist
				
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

	@Override
	public boolean closeFile() {
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
	

	public boolean getBoolean() {
		return this.changeOccurred;
	}

	public String getRemoteAddress(){
		return this.remoteAddress;
	}
	
	
}
