//-----------------------------------
//	Keith Kenneally CS3
// 	R00142850
//-----------------------------------
package Part2_Sockets;
import javafx.scene.control.Label;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.*;
public class ConfirmBox {

	public static void display(String message) {
		Stage window = new Stage();
		
		//we are gonna block user interaction with other windows until this one is taken care of i.e alert box
		window.initModality(Modality.APPLICATION_MODAL);
		window.setMinWidth(450);
		
		Label label = new Label(message);
		//label.setTextFill(Color.RED);
		label.setTextFill(Color.GREEN);
		label.setFont(new Font("Arial", 20));
		
		//label.setText(message);
		Button closeButton = new Button("Close the window");
		closeButton.setOnAction(e -> window.close());
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		///Display the window and wait till 
		window.showAndWait();
		
	}

}
