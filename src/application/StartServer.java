package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import rs.raf.pds.v4.z5.ChatClient;
import rs.raf.pds.v4.z5.ChatServer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;



public class StartServer extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			/*FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
		    Parent root;
		    root = loader.load();
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.show();*/
			
			ChatServer chatServer = new ChatServer(4555);
			chatServer.start();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
