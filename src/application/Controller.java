package application;



import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.*;

public class Controller {
	
	@FXML
	private TextField textField;
	
	@FXML
	VBox messageContainer;
	public TextField getTextField() {
        return textField;
    }
	@FXML
	private void initialize() {
	    System.out.println("messageContainer: " + messageContainer);
	}
	public boolean buttonClicked = false;
	@FXML
	public void onButtonClicked(ActionEvent event)
	{
		Stage mainWindow = (Stage) textField.getScene().getWindow();
		String text = textField.getText();
		//mainWindow.setTitle(text);
		addMessage(text);
		buttonClicked = true;
		//textField.clear();
	}
	public void addMessage(String message) {
	    Label senderLabel = new Label("you");
	    senderLabel.setStyle("-fx-font-weight: bold;");
	    
	    Label messageLabel = new Label(message);
	    messageLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px;");
	    
	    HBox messageBox = new HBox(senderLabel, messageLabel);
	    messageBox.setAlignment(Pos.CENTER_RIGHT);
	    HBox.setMargin(messageBox, new Insets(5, 5, 5, 5));

	    ContextMenu contextMenu = new ContextMenu();
	    MenuItem editMenuItem = new MenuItem("Edit");
	    editMenuItem.setOnAction(e -> onEditClicked(messageLabel));
	    contextMenu.getItems().add(editMenuItem);

	    messageBox.setOnMouseClicked(event -> {
	        if (event.getButton() == MouseButton.SECONDARY) {
	            contextMenu.show(messageBox, event.getScreenX(), event.getScreenY());
	        }
	    });
	    messageContainer.getChildren().add(messageBox);
	}

	public void onMessageClicked(MouseEvent event) {
	    if (event.getButton() == MouseButton.SECONDARY) {
	        onEditClicked((Label) event.getSource());
	    }
	}
	private void onEditClicked(Label messageLabel) {
	    TextInputDialog dialog = new TextInputDialog(messageLabel.getText());
	    dialog.setTitle("Edit Message");
	    dialog.setHeaderText(null);
	    dialog.setContentText("Edit your message:");

	    Optional<String> result = dialog.showAndWait();
	    result.ifPresent(editedMessage -> messageLabel.setText(editedMessage + " (Ed)"));
	}
	
	public void showMessage(String message,String sender) {
	    Label serverLabel = new Label(sender);
	    serverLabel.setStyle("-fx-font-weight: bold;");

	    Label messageLabel = new Label(message);
	    messageLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px;");

	    VBox messageBox = new VBox(serverLabel, messageLabel);
	    VBox.setMargin(messageBox, new Insets(5, 5, 5, 5));

	    messageContainer.getChildren().add(messageBox);
	}
}
