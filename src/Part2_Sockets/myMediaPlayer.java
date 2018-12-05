//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package Part2_Sockets;

import javafx.scene.paint.Color;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
//import java.awt.Color;
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
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import Student.StudentClient.ButtonListener;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	private String localAddress = "C:\\WorkSpace\\MediaPlayerA01\\src\\Part2_Sockets\\localVids";
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
	
	private Scene scene; 	// scene for the media player
	private Scene sceneFiles; 	// scene for the folders
	private HBox allButtons = new HBox();

	private String name;

	private MediaPlayer mediaPlayer;
	private MediaControl mediaControl = new MediaControl();
	private BorderPane mvBorderPane ;
	 private Label changeDetected = new Label("Change detected in remote");

	private Stage window;
	final ExecutorService service = Executors.newCachedThreadPool();

	//  private Button btRegister = new Button("Register to the Server");
	  private int getMenuCommand;

	  String host = "localhost";
	  Socket socket;
	  ObjectInputStream objectInput = null;
	  ObjectOutputStream objectOutput = null;

	  DataOutputStream toServer = null;
	  //DataInputStream fromServer = null;
	  
	  boolean startListening = false;
	public myMediaPlayer(){
		
		service.submit(new Runnable(){
			@Override
			public void run() {

				while(!stopExecutor){
					
				try{
						
					Socket socketListener = new Socket("localhost", 9002);
						
						DataInputStream input = new DataInputStream(socketListener.getInputStream()); //Error Line!
						System.out.println("Listening to changes");
						
					
						int isChanged = input.readInt();
						//System.out.println("is changed in client is "+ isChanged);
						
						if(isChanged == 1){ // a change has occured
							System.out.println("Change found");
							
							useMenuSystem(1);   // send number 1, which will getNames from server through a socket
							displayTempGUImessage();  // this method displays a GUI message for 5 seconds saying a change has occured
													    
						}else{
							// no change do nothing
						}
					
					   	updateLocalChanges();						
					    Thread.sleep(2000);
					      
					}											
					catch(InterruptedException v ){
						
					} catch (UnknownHostException e) {
						System.out.println("Server not online");
					} catch (IOException e) {
					
						System.out.println("Server not online");

					}
				}
				}						
		});
	}
	final TextField textField = new TextField();

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage;
		final Button playButton = new Button("Play");
		final Button removeButton = new Button("Delete");
		final Button submit = new Button("Submit");
		final TextArea ta = new TextArea();
		
		textField.setPrefSize(170, 25);
		textField.setPromptText("Enter command..");
		
		ta.setText("Enter 1 to get names\nEnter 2 download\nEnter 3 to upload\nEnter 4 to disconnect from server");
		ta.setPrefSize(304, 114);
		
		ta.setEditable(false);

		playButton.setVisible(false);
		removeButton.setVisible(false);
		
		window.setTitle("Embedded Media Player");
		 
		  HBox hbox = new HBox();
		  VBox vbox = new VBox();
		  
		//  Label changeDetected = new Label("Change detected in remote");
		  changeDetected.setVisible(false);
		  changeDetected.setStyle("-fx-font: 14 arial;");

		  Label remoteLabel = new Label("      Remote Server");
		  Label spacer = new Label("                                 	");
		  Label spacer2 = new Label("\t\t\t\t\t");

		  Label localLabel = new Label("Local Server");
		 
		  hbox.getChildren().addAll(remoteLabel,spacer,spacer2,localLabel);
		  hbox.setPadding(new Insets(140,1,16,36));
		 
		  VBox bottomVbox = new VBox();
		  bottomVbox.getChildren().addAll(textField, submit);
		  bottomVbox.setSpacing(10);

		  HBox bottomHbox = new HBox();
		  bottomHbox.getChildren().addAll(ta, bottomVbox);
		  bottomHbox.setSpacing(20);
		
		  vbox.getChildren().addAll(playButton, removeButton,changeDetected, bottomHbox);
		  vbox.setAlignment(Pos.CENTER);
		  vbox.setSpacing(20);
		  hbox.setSpacing(100);
		  bp = new BorderPane();
		  		
		  this.getInitialListView();
		  
		  bp.setTop(hbox);  
		  bp.setCenter(vbox);
		  bp.setLeft(remoteList);
		  bp.setRight(localList);
		  
		  sceneFiles = new Scene(bp, 1100, 700);
		  sceneFiles.getStylesheets().add(myMediaPlayer.class.getResource("application.css").toExternalForm());
		  
		  // ------	 IF A FILE IN LISTVIEW (LOCAL FILES) ARE CLICKED, THEN DO SOMETHING
		  localList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			    @Override		// if a local file is clicked, give the option to play it in the media player
			    public void handle(MouseEvent click) {

			        if (click.getClickCount() == 1) { // if it is clicked..
			        	//System.out.println("Mouse clicked");
			        	playButton.setVisible(true);
			        	removeButton.setVisible(true);
			        	
			        }}});
		  
		  // IF A FILE IN LISTVIEW (REMOTE FILES) ARE CLICKED, THEN DO SOMETHING
		  remoteList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent click) {

			        if (click.getClickCount() == 1) {
			        	playButton.setVisible(false);
			        	removeButton.setVisible(false);			        	
			            bottomHbox.setVisible(true);
           
			        }}});
								
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
			  	System.out.println("removing file name "+ name);
			  try{
				  bfile.delete();
				  ConfirmBox.display("File has been removed from your system");

			  }catch(Exception l){
				  System.out.println("Failed to remove");
			  }
		  			  
		  });
		
		  submit.setOnAction(e-> {
			  
			  String s = textField.getText();
			
			  // only accept values from the menu. ie 1 2 3 4
			  if(s.equalsIgnoreCase("1") || s.equalsIgnoreCase("2") || s.equalsIgnoreCase("3") || s.equalsIgnoreCase("4")){
			  
				  getMenuCommand = Integer.parseInt(s);
				  useMenuSystem(getMenuCommand);
				  
			  }else{
				  FailedBox.display();
				  s = null;
			  }
			 
		  });
		
			bp.setId("imagetest");
	        window.setScene(sceneFiles);     
	        window.setMinWidth(1110);
			window.setMaxWidth(1110);
			window.setMinHeight(760);
			window.setMaxHeight(760);
	        window.show();
	      	       
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
	private void displayTempGUImessage() {
		final Thread thisThread = Thread.currentThread();
		final int timeToRun = 3000; // display the message on the GUI for 8 seconds, then disappear

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
		changeDetected.setVisible(false); 
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
            	 try {

                 Object object = null;
				try {
					object = objectInput.readObject(); // read the object thoough the socket connected to server

				} catch (IOException e) {
					e.printStackTrace();
				}
                 remoteArr.clear();

                 remoteArr =  (ArrayList<String>) object;
             	remoteObsList = FXCollections.observableArrayList(remoteArr);
             	remoteList.setItems(remoteObsList);  //interactive list of file names
			                        
             } catch (ClassNotFoundException e) {
                 System.out.println("Class not found exception");
                // e.printStackTrace();
             }           		
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
            		System.out.println("Null pointer caught");
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
	
	public void shutdown(){
		
		this.stopExecutor =  true;
		service.shutdown();

	}
	
	InputStream inStream = null;
	OutputStream outStream = null;
	
	public boolean openFile( String fileName) { 
		
		File afile = new File(this.localAddress +"\\"+  fileName);
 	
	    try {
			inStream = new FileInputStream(afile);
			return true;
	    
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}   
	}


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
	
	
	public boolean closeFile(byte [] b, String address, String name) {
		
		
		try {
			// THIS IS FOR DOWNLOADING A FILE
			File f = new File(address+ "\\"+name);
			outStream = new FileOutputStream(f);

			if(address == this.localAddress){ // if this then I am downloading not uploading
				outStream.write(b);
			}
			else{
				// THIS IS FOR UPLOADING A FILE
				outStream.write(getB());
				
			}
		   			
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	Thread one;
	public void useMenuSystem(int num){
		 
		 objectInput = null;
		 toServer = null;
		 
		one = new Thread() {
		    public void run() {
		        try {

		        	try {

						Socket socket = new Socket("localhost", 8002);
						
						objectInput = new ObjectInputStream(socket.getInputStream()); //Error Line!
					
						objectOutput = new ObjectOutputStream(socket.getOutputStream());
					
						toServer = new DataOutputStream(socket.getOutputStream());
				    		
					} catch (UnknownHostException e1) {
						System.err.println("Unknown host");
					} catch (IOException e2) {
						System.err.println("Failed to connect to server.");				
		        	} 
			        
			  
			    	try {
			    		toServer.writeInt(num);
			            toServer.flush();

			    		switch (num){
			    	    case 1: 							
			    	    	updateRemoteChanges();

			    	        break;
			    	    case 2: 
							  downloadFile();
									    	    	
			    	        break;
			    	    case 3:
			    	    	uploadFile();
			    	    	
			    	    	break;
			    	    
			    	    case 4:
			    	    	objectInput.close();
							
							objectOutput.close();
						
							toServer.close();
			    	    	break;
			    	   
			    	}

						} catch (IOException ee) {
							System.out.println("Error in client");
						}
				        catch(NullPointerException c){
					    	  System.out.println("Null pointer Error caught in client");
					      }
					
				      
		            Thread.sleep(1000);
		        	        
		        } catch(InterruptedException v) {
		            System.out.println(v);
		        }
		    }  
		};

		one.start();
			    	
		 }

	
	public void downloadFile(){
		name = remoteList.getSelectionModel().getSelectedItem();	//get the file name from the listview				
		if(remoteList.getSelectionModel().getSelectedItem() != null){
			
            	 try {
                 Object object = null;
                                
                	 toServer.writeUTF(name);
                	 toServer.flush();
                 
				try {
					object = objectInput.readObject();
					
					 byte [] bytes =  (byte[]) object; // read in the byte array from server
					    closeFile(bytes, getLocalPath(), name);
					    updateLocalChanges();

					
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				                      
             } catch (IOException e) {
                 System.out.println("The title list has not come from the server");
                 e.printStackTrace();
             }
            	
		}// if not selected
		else{
			System.out.println("Please chose a file");
			textField.clear();
			textField.setPromptText("Chose a file");

			}
      //  }});
	}
	public void uploadFile(){
		System.out.println("in upload"); 
 
		name = localList.getSelectionModel().getSelectedItem();	//get the file name from the listview				
		  
		  if(localList.getSelectionModel().getSelectedItem() != null){               
                 try {

                	  toServer.writeUTF(name);
                	  toServer.flush();
              	 
                      this.openFile( name );

					  objectOutput.writeObject(this.getB());

				} catch (IOException e) {					
					e.printStackTrace();
				} 
		  } 		           
	}
	 public static void main(String[] args) {					
		 
	     
		 launch(args);	
		}
}
