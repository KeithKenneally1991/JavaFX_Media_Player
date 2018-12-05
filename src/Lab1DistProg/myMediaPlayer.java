//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package Lab1DistProg;
import javafx.scene.paint.Color;

import java.io.ByteArrayOutputStream;
//import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.media.MediaPlayer.Status;

public class myMediaPlayer extends Application {

	protected MediaPlayer mp;
	protected MediaView mediaView;
	protected final boolean repeat = false;
	protected boolean stopRequested = false;
	protected boolean atEndOfMedia = false;
	protected Duration duration;
	protected Slider timeSlider;
	protected Label playTime;
	protected Slider volumeSlider;
	public HBox mediaBar;
	    
    final Button play = new Button("Play");
    final private Button fast = new Button("Fast");
    final private Button slow = new Button("Slow");
    final private Button stop = new Button("Stop");
    final private Button exit = new Button("Exit"); 
	
    // -------------------------------------------------------------------------------
    // location of the folders
   // String remoteAddress = "C:\\WorkSpace\\Semester 5\\src\\Lab1DistProg\\remoteVids\\";
	private String localAddress = "C:\\WorkSpace\\MediaPlayerA01\\src\\Lab1DistProg\\localVids";
    //---------------------------------------------------------------------------------
    
	private ArrayList<String> localArr = new ArrayList();
	private ArrayList<String> remoteArr = new ArrayList();

	private ListView<String> localList;   //interactive list of file names
	private ListView<String> remoteList;   //interactive list of file names
	
	private BorderPane bp;
	
	private ObservableList<String> list;
	private ObservableList<String> remoteObsList;
	private boolean stopWasRequested = false;
	private boolean stopExecutor= false;


	static  monitorFolder monitor =  monitorFolder.getInstance();
	
	private Scene scene; 	// scene for the media player
	private Scene sceneFiles; 	// scene for the folders
	private HBox allButtons = new HBox();

	//String MEDIA_URL = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_2mb.mp4";
	private String name;

	private MediaPlayer mediaPlayer;
	private MediaControl mediaControl = new MediaControl();
	private BorderPane mvBorderPane ;
		 
	private Stage window;
	final ExecutorService service = Executors.newCachedThreadPool();

	public myMediaPlayer(){
		
		service.submit(new Runnable(){
			@Override
			public void run() {
				while(!stopExecutor){
					try{
						Thread.sleep(3000);
					}catch(InterruptedException e){
						
					}
					updateLocalChanges();						

				}
			}
		});
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage;
		final Button playButton = new Button("Play");
		final Button removeButton = new Button("Delete");
		final Button uploadButton = new Button("Upload");

		playButton.setVisible(false);
		removeButton.setVisible(false);
		uploadButton.setVisible(false);

        final Button transferFile = new Button("Download");
        transferFile.setVisible(false);
		window.setTitle("Embedded Media Player");
		 
		  HBox hbox = new HBox();
		  VBox vbox = new VBox();
		  
		  Label changeDetected = new Label("Change detected in remote");
		  changeDetected.setVisible(false);
		  changeDetected.setStyle("-fx-font: 14 arial;");

		  
		  Label remoteLabel = new Label("      Remote Server");
		  Label spacer = new Label("                                 	");
		  Label spacer2 = new Label("\t\t\t\t\t");

		  Label localLabel = new Label("Local Server");
		 
		  hbox.getChildren().addAll(remoteLabel,spacer,spacer2,localLabel);
		  hbox.setPadding(new Insets(140,1,16,36));
		 
		 // HBox leftBox = new HBox();
		  vbox.getChildren().addAll(transferFile,playButton, removeButton, uploadButton,changeDetected);
		  vbox.setAlignment(Pos.CENTER);
		  vbox.setSpacing(50);
		  hbox.setSpacing(100);
		  bp = new BorderPane();
		  		
		 // this.getRemoteNames(); //call the method which gets the names if files stored in the folder
		  this.getInitialListView();
		  
		  bp.setTop(hbox);  
		  bp.setCenter(vbox);
		  bp.setLeft(remoteList);
		  bp.setRight(localList);
		  
		  //sceneFiles = new Scene(bp, 1100, 700);
		  sceneFiles = new Scene(bp, 1100, 700);

		 // this.mediaView();
		  sceneFiles.getStylesheets().add(myMediaPlayer.class.getResource("application.css").toExternalForm());
		  
		  // ------	 IF A FILE IN LISTVIEW (LOCAL FILES) ARE CLICKED, THEN DO SOMETHING
		  localList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			    @Override		// if a local file is clicked, give the option to play it in the media player
			    public void handle(MouseEvent click) {

			        if (click.getClickCount() == 1) { // if it is clicked..
			        	//System.out.println("Mouse clicked");
			        	playButton.setVisible(true);
			        	removeButton.setVisible(true);
			        	uploadButton.setVisible(true);

			            transferFile.setVisible(false);

			        }}});
		  
		  // IF A FILE IN LISTVIEW (REMOTE FILES) ARE CLICKED, THEN DO SOMETHING
		  remoteList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent click) {

			        if (click.getClickCount() == 1) {
			        	playButton.setVisible(false);
			        	removeButton.setVisible(false);
			        	uploadButton.setVisible(false);
			            transferFile.setVisible(true);
			                
			        }}});
				transferFile.setOnAction(e-> {
				  name = remoteList.getSelectionModel().getSelectedItem();	//get the file name from the listview				
					try{
						    monitor.openFile( name ); // send the file name to monitorFolder and specify it is to be downloaded
						    closeFile(this.getLocalPath(), name);
							
					}catch(NullPointerException m){
					    // do nothing	
					}	  
			});
				
		  playButton.setOnAction(e-> {
				
		  name = localList.getSelectionModel().getSelectedItem();	//get the file name from the listview				
		
		  try{
			  this.playMedia(name);	// feed the file name into the media player
			  mp.setAutoPlay(true);

		  }catch(MediaException m){
			  System.out.println("Unsuppported media");
		  }
		  });
		  
		  // ---------- can remove a file from your local direcetory only,, not from the remote server ----------
		  removeButton.setOnAction(e ->{
			  name = localList.getSelectionModel().getSelectedItem();	//get the file name from the listview				
			  File bfile =new File(this.localAddress+"\\"+name);	

			  try{
				  bfile.delete();
				  ConfirmBox.display("File has been removed from your system");

			  }catch(Exception l){
				  System.out.println("Failed to remove");
			  }
		  
			  
		  });
		  uploadButton.setOnAction(e ->{
			    name = localList.getSelectionModel().getSelectedItem();	//get the file name from the listview					
			  	try{
			  		boolean open = openFile(monitor.getRemoteAddress(), name);     // open the file stream,  "upload" is needed to differentiate between downlading and uploading
				  	//getB();
				    closeFile(monitor.getRemoteAddress(), name);
				  	monitor.ckeckBool(open, "File successfully uploaded to a shared server!");
			  	}catch(NullPointerException p){
			  	    // do noting	
			  	}    
		  });
		
			bp.setId("imagetest");
	        window.setScene(sceneFiles);     
	        window.setMinWidth(1110);
			window.setMaxWidth(1110);
			window.setMinHeight(760);
			window.setMaxHeight(760);
	        window.show();
	        System.out.print("Listening for changes.");
	         monitor.addListener(new listListener(){
					boolean printChangedLabel = false;

			@Override
			public void checkForChanges() {
				changeDetected.setVisible(false);
		        System.out.print(".");
		       			

		        if(monitor.getBoolean() == true) {
					
		        	//updateRemoteChanges(); // a change has occured in the remote server so update the listview

		        	//-----------------------------------------------------------
		        	// THIS NEXT THTREAD HAS NOTHING GOT TO DO WITH THE REMOTE LIST. THIS NEXT THREAD IS FOR
		        	// DISPLAYING A MESSAGE ON SCREEN FOR 5 SECONDS WHEN A CHANGE HAS OCCURED
		        	//----------------------------------------------------------------------
		        	updateRemoteChanges(); // a change has occured in the remote server so update the listview
		        	final Thread thisThread = Thread.currentThread();
					final int timeToRun = 8000; // display the message on the GUI for 8 seconds, then disappear

					new Thread(new Runnable() {
					    public void run() {
					        try {
								thisThread.sleep(timeToRun);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					        thisThread.interrupt();
					    }
					}).start();

					while (!Thread.interrupted()) {
						changeDetected.setVisible(true); // set the label visibility to true for a period of time	
					} 					
		        }
			}
        });
	      
	       
	         //------------------------------------------------------------------------------
			//  ------------GUI FOR MEDIA PLAYER --------------------------------------------
			mvBorderPane = new BorderPane();
		  	this.mvBorderPane.setId("mediaControl");

	    	scene = new Scene(mvBorderPane, 1100, 720); 
	    	//this.mediaView
		    scene.getStylesheets().add(myMediaPlayer.class.getResource("application.css").toExternalForm());
			  
	        mediaBar = new HBox();
	        mediaBar.setAlignment(Pos.CENTER);
	        mediaBar.setPadding(new Insets(5, 10, 5, 10));
	       
	        allButtons.getChildren().addAll(play, fast, slow, stop,exit);
	        allButtons.setSpacing(6);
	        allButtons.setId("customPlayerButtons");
	        mediaBar.setId("customMediaBar");
	        
	        mediaBar.getChildren().add(allButtons);
	        // Add spacer
	        Label spacermp = new Label("   ");
	        mediaBar.getChildren().add(spacermp);

	        // Add Time label
	        Label timeLabel = new Label("Time: ");
	        mediaBar.getChildren().add(timeLabel);
	        playButton.setPrefSize(44,22);
	        // Add time slider
	        timeSlider = new Slider();
	        HBox.setHgrow(timeSlider, Priority.ALWAYS);
	        timeSlider.setMinWidth(50);
	        timeSlider.setMaxWidth(199);

	        timeSlider.valueProperty().addListener(new InvalidationListener() {
	            public void invalidated(Observable ov) {
	                if (timeSlider.isValueChanging()) {
	                    // multiply duration by percentage calculated by slider position
	                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
	                }
	            }
	        });
	        mediaBar.getChildren().add(timeSlider);

	        // Add Play label
	        playTime = new Label();
	        playTime.setPrefWidth(130);
	        playTime.setMinWidth(50);
	        mediaBar.getChildren().add(playTime);

	        // Add the volume label
	        Label volumeLabel = new Label("Vol: ");
	        mediaBar.getChildren().add(volumeLabel);

	        // Add Volume slider
	        volumeSlider = new Slider();
	        volumeSlider.setPrefWidth(70);
	        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
	        volumeSlider.setMinWidth(30);
	        volumeSlider.valueProperty().addListener(new InvalidationListener() {
	            public void invalidated(Observable ov) {
	                if (volumeSlider.isValueChanging()) {
	                    mp.setVolume(volumeSlider.getValue() / 100.0);
	                }
	            }
	        });
	        mediaBar.getChildren().add(volumeSlider);

	        mediaBar.setPrefSize(1000, 80);
	        mvBorderPane.setBottom(mediaBar);
	        
	        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
	        	
	        	@Override
	        	public void handle(WindowEvent event){
	        		
	        		Platform.exit();
	        		System.exit(0);

	              //  shutdown();
	        	//	monitor.shutdown();
	        		
	        	}
			});
	        
	}
	
	private void playMedia(String name){
		
		   File file = new File(this.localAddress+"\\"+ name);
		   String m = file.toURI().toString();	
		   
		   this.player(this.initialize(m));
	       window.setScene(scene);
	      
	}
	private  MediaPlayer initialize(String m){
		Media media = new Media (m);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        return mediaPlayer;
	}
	private void player (final MediaPlayer mp)   {
		
    	this.mp = mp;
        mediaView = new MediaView(mp);
        mvBorderPane.setCenter(mediaView);
        mediaView.setFitHeight(580);
        mediaView.setFitWidth(1000);

        mvBorderPane.setStyle("-fx-background-color: black;");
    	
        play.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
        	Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        mp.play();
                        atEndOfMedia = false;
                    }
                    mp.play();
                } else {
                    mp.pause();
                    
                }
            }
        });
        
        fast.setOnAction(e->{	// speed up the video
        	mp.setRate(2);
        	
        });
        
        slow.setOnAction(e->{	// slow down the video
        	mp.setRate(.5);

        });
        stop.setOnAction(e -> { // stop the video
        	mp.stop();
        });  
        exit.setOnAction(e -> { //go back to the scene which displays file names
               //mp.getOnStopped();   
               mediaView.getMediaPlayer().stop();
               mp.stop();
               this.switchScene();
        	
        });
      
        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                mediaControl.updateValues( mp, duration, timeSlider,playTime,  volumeSlider);

            }
        });
        mp.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mp.pause();
                    stopRequested = false;
                } else {
                    play.setText("||");

                }
            }
        });
        
        mp.setOnPaused(new Runnable() {
            public void run() {
               // System.out.println("onPaused");
                play.setText(">");
        		mp.setRate(1); // reset the speed to original if its been changed
            }
        });

        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                mediaControl.updateValues(mp, duration, timeSlider,playTime,  volumeSlider);
               // mediaControl.updateValues();

            }
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 5);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    play.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });
         
    }
	 
	/*public String fileChooser(){
		System.out.println("Choosing a file to play..");
		FileChooser 	file = new FileChooser();
		File 			selectedFile = file.showOpenDialog(null);
		String fileToPlay = selectedFile.toURI().toString();
		
		// when I convert the file path to a string, it was auotmatically puttin the word "file" at the start. This was
		// causing problems when I transferring the file, so here I use substring to ignore the first 5 letters of the path
		fileToPlay  = 	fileToPlay.substring(6);
		
		System.out.println("File chosen is\t"+fileToPlay);
		return 	fileToPlay;
	}*/
	
	// this method updates the remote list listview when the listListener detects a change	
	public void updateRemoteChanges(){
		Platform.runLater(new Runnable() {
            @Override public void run() {
                //System.out.print("Change Detected: Remote Server");

            	remoteArr.clear();
            	remoteArr = monitor.getRemoteNames(); // arrayList which stores files from remote folder
            	remoteObsList = FXCollections.observableArrayList(remoteArr);
            	remoteList.setItems(remoteObsList);  //interactive list of file names
		
        }});
		
		
	}
	public void getInitialListView() throws InvocationTargetException{

		// 		This class knows about the local files. So store them in an arraylist
		File folder = new File(this.getLocalPath());  
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	   	
		    	  localArr.add(i, listOfFiles[i].getName()); // store the file names in arraylist
		    	  
		      } else if (listOfFiles[i].isDirectory()) {
		    	  // do nothing
		      }
		    }
		     
		try{	 
		
			list = FXCollections.observableArrayList(localArr);
			localList = new ListView<String>(list);   //interactive list of file names
			
			// now ask the monitorFolder for a list of remote files
			remoteArr = monitor.getRemoteNames(); // arrayList which stores files from remote folder
			remoteObsList = FXCollections.observableArrayList(remoteArr);
			remoteList = new ListView<String>(remoteObsList);   //interactive list of file names
			
		}
		catch(IndexOutOfBoundsException c){
		}
}

	public void updateLocalChanges(){ 	// update local listview dynamically when a file have been moved

		Platform.runLater(new Runnable() {
            @Override public void run() {
            	try{
                   
            	localArr.clear();
                         		
            		File folder = new File(getLocalPath());  
            		File[] listOfFiles = folder.listFiles();

            		    for (int i = 0; i < listOfFiles.length; i++) {
            		      if (listOfFiles[i].isFile()) {
            		    	   	
            		    	  localArr.add(i, listOfFiles[i].getName()); // store the file names in arraylist
            		    	  
            		      } else if (listOfFiles[i].isDirectory()) {
            		    	  // do nothing
            		      }
            		    }
                list = FXCollections.observableArrayList(localArr);
            	localList.setItems(list);
            	
  
            	}catch(NullPointerException e){
            		// do nothing
            	}
            	
            }
        });
			     
	}
	public  void switchScene(){
		window.setScene(sceneFiles);
		
	}
	public String getLocalPath(){
		return this.localAddress;
	}
	public static void main(String[] args) {					
		launch(args);	
	}
	
	
	public void shutdown(){
		
		this.stopExecutor =  true;
		service.shutdown();

	}
	InputStream inStream = null;
	OutputStream outStream = null;
public boolean openFile(String destination, String fileName) { 
		
		File afile = new File(this.localAddress +"\\"+  fileName);
 	
	    try {
			inStream = new FileInputStream(afile);
			return true;
	    
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	    
	}

// ----------------------------------------------------------
// THIS METHOD IS NOT USED FOR DOWNLOADING A FILE
public byte[] getB() {
	 
	 ByteArrayOutputStream bos = new ByteArrayOutputStream();
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
	
	
	public boolean closeFile(String address, String name) {
		
		
		try {
			// THIS IS FOR DOWNLOADING A FILE
			File f = new File(address+ "\\"+name);
			outStream = new FileOutputStream(f);

			if(address == this.localAddress){ // if this then I am downloading not uploading
				outStream.write(monitor.getB());
			    monitor.closeFile();	
			}
			else{
				// THIS IS FOR UPLOADING A FILE
				outStream.write(getB());
			}
		   
			inStream.close();
		    outStream.close();
		    monitor.closeFile();
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
