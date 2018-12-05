
//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------

package RMI_Dist_Prog;


import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.scene.text.Font;
public class FailedBox{

	
	public static void display() {
		Stage window = new Stage();
		
		// block user interaction with other windows until this one is taken care of i.e alert box
		window.initModality(Modality.APPLICATION_MODAL);
		//window.setTitle(title);
		window.setMinWidth(450);
		
		Label label = new Label("Action Failed, File already exists in your local server");
		Label message = new Label();
		//message.setText(str);
		message.setText("Remove old file first or chose a different file");

		label.setTextFill(Color.RED);
		label.setFont(new Font("Arial", 20));

		//label.setText(message);
		Button closeButton = new Button("Close the window");
		closeButton.setOnAction(e -> window.close());
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label,message, closeButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		///Display the window and wait for user interaction
		window.showAndWait();
		

	}

}
