package view;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.MyModel;
import vm.ViewModel;
//import model.MyModel;
//import vm.ViewModel;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setResizable(false);
			primaryStage.setTitle("Plane Control Center");
			
			MyModel m = new MyModel();
			ViewModel vm = new ViewModel(m);
			m.addObserver(vm);
			
			FXMLLoader fxml = new FXMLLoader();
			BorderPane root = fxml.load(getClass().getResource("MainWindowController.fxml").openStream());
			
			MainWindowController mwc = (MainWindowController)fxml.getController();
			mwc.setStage(primaryStage);
			mwc.setViewModel(vm);
			
			vm.addObserver(mwc);
			
			Scene scene = new Scene(root,930,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
