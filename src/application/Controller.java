package application;



import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import rs.raf.pds.v4.z5.ChatClient;
import javafx.scene.layout.*;

public class Controller {
	
	@FXML
	private TextField textField;
	private ChatClient chatClient;
	@FXML
	VBox messageContainer;
	public TextField getTextField() {
        return textField;
    }
	
	public boolean buttonClicked = false;
	public void setChatClient(ChatClient chatClient)
	{
		this.chatClient = chatClient;
	}
	@FXML
	public void onButtonClicked(ActionEvent event)
	{
		
		String text = textField.getText();
		addMessage(text);
		buttonClicked = true;
		//textField.clear();
	}
	public void addMessage(String message) {
	    Label senderLabel = new Label("you");
	    senderLabel.setStyle("-fx-font-weight: bold;");
	    
	    Label messageLabel = new Label(message);
	    messageLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px;");
	    
	    Button replyButton = new Button("Reply");
	    replyButton.setOnAction(e -> onReplyClicked(messageLabel));
	    
	    HBox messageBox = new HBox(senderLabel, messageLabel,replyButton);
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
	    result.ifPresent(editedMessage -> {
	    	String edited = editedMessage + " (Ed)";
	    	
	    	chatClient.sendEditedMessage(messageLabel.getText(),edited);
	    	messageLabel.setText(edited);
	    	
	    });
	}
	
	public void showMessage(String message,String sender) {
	    Label serverLabel = new Label(sender);
	    serverLabel.setStyle("-fx-font-weight: bold;");

	    Label messageLabel = new Label(message);
	    messageLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px;");
	    Button replyButton = new Button("Reply");
	    replyButton.setOnAction(e -> onReplyClicked(messageLabel));
	    VBox messageBox = new VBox(serverLabel, messageLabel,replyButton);
	    VBox.setMargin(messageBox, new Insets(5, 5, 5, 5));

	    messageContainer.getChildren().add(messageBox);
	}
	private void onReplyClicked(Label messageLabel) {
	    TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Reply to Message");
	    dialog.setHeaderText(null);
	    dialog.setContentText("Reply:");

	    Optional<String> result = dialog.showAndWait();
	    result.ifPresent(reply -> {
	       
	        addReply(messageLabel, reply);
	       // textField.clear();
	        
	    });
	    
	    
	    
	    
	}

	private void addReply(Label originalMessageLabel, String reply) {
	    Label senderLabel = new Label("you (reply) to " + originalMessageLabel.getText());
	    senderLabel.setStyle("-fx-font-weight: bold;");

	    Label replyLabel = new Label(reply);
	    replyLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px;");

	    HBox replyBox = new HBox(senderLabel, replyLabel);
	    replyBox.setAlignment(Pos.CENTER_RIGHT);
	    HBox.setMargin(replyBox, new Insets(5, 5, 5, 5));

	    messageContainer.getChildren().add(replyBox);
	    chatClient.sendReplyMessage(originalMessageLabel.getText(),"Replied to " + originalMessageLabel.getText() + " with " + reply);
	   
	    // textField.setText("Replied to " + originalMessageLabel.getText() + " with " + reply);
       // buttonClicked = true;
	  
	}

}
