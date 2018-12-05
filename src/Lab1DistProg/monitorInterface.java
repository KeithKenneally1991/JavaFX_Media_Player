//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package Lab1DistProg;

import java.io.File;
import java.util.ArrayList;

public interface monitorInterface {

	
    void 	ckeckBool (boolean bool, String messsage);
   // ArrayList 	getLocalNames();	// get names of files stored in local folder
    public ArrayList getRemoteNames(); //get names of files stored in remote folder
    boolean 	openFile( String fileName);	// opens a file called name
    byte [] 		getB ();	//Gets a byte from the currently open file 
    boolean 	closeFile();		// closes the stream

}
