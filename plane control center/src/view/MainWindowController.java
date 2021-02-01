package view;

// correct bind

import java.io.*;
import java.net.URL;
import java.util.*;
import expression.Number;

import javafx.beans.value.ChangeListener;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import view.Joystick.Point;
import vm.ViewModel;

public class MainWindowController implements Initializable, Observer{
	//data_members:
	private ViewModel vm;
	
	@FXML	
	private Map m;
	
	@FXML
	private Joystick j;
	
	@FXML
	private TextArea txtArea;
	
	@FXML
	private RadioButton radioAuto;
	
	@FXML
	private RadioButton radioManual;
	
	@FXML
	private ScrollBar scrollBarStands;
	
	@FXML
	private ScrollBar scrollBarLays;
	
	private Stage stage;
	private Stage dialog;
	private int windowNum;
	
	private BooleanProperty isConnectedToSimulator;
	
	// constructor, initialize, update, setting:
	
	public MainWindowController() {
		stage = null;
		isConnectedToSimulator = new SimpleBooleanProperty();
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
		
	}
	
	public void setViewModel(ViewModel vm)
	{
		this.vm = vm;
		
		Platform.runLater(()->{
			vm.getiPlane().bindBidirectional(m.getiPlane());
			vm.getiX().bindBidirectional(m.getiX());
			vm.getjPlane().bindBidirectional(m.getjPlane());
			vm.getjX().bindBidirectional(m.getjX());
			vm.getAutoPilotCode().bindBidirectional(txtArea.textProperty());
			vm.getAileron().bindBidirectional(j.getXProperty());
			vm.getElevator().bindBidirectional(j.getYProperty());
			vm.getThrottle().bindBidirectional(scrollBarStands.valueProperty());
			vm.getRudder().bindBidirectional(scrollBarLays.valueProperty());
			vm.getIsConnectedToSimulator().bindBidirectional(isConnectedToSimulator);
			vm.getHeading().bindBidirectional(m.getHeading());
			vm.openDataServer();
		});
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o==vm)
		{
			if(arg.equals("matrix"))
			{
				m.setMap(vm.getMatrix());
			}
			else if(arg.equals("path"))
			{
				m.setPoints(vm.getPoints());
			}
			else if(arg.equals("error"))
			{
				if(vm.getError().get().equals("invalid ip"))
					error(dialog);
				else if(vm.getError().get().equals("invalid port"))
					error(dialog);
				else	
					error(stage);
			}
			else if(arg.equals("plain location"))
			{
				new Thread(()->m.draw()).start();
			}
		}
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
			Platform.runLater(()->{
				startPopUp();
				
				stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

					@Override
					public void handle(WindowEvent event) {
						vm.end();
						vm.disConnectFromSimulator();
					}
				});
				
				radioAuto.selectedProperty().addListener(new ChangeListener<Boolean>() {
				    @Override
				    public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
				        if (isNowSelected) { 
				        	if(!isConnectedToSimulator.get())
				        	{
				        		vm.getError().set("you must connect first");
				        		error(stage);
				        		radioAuto.setSelected(false);
				        	}

				        } else {
				        	//System.out.println("no auto");
				        }
				    }
				});
				
				radioManual.selectedProperty().addListener(new ChangeListener<Boolean>() {
				    @Override
				    public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
				        if (isNowSelected)
				        { 
				        	if(!vm.connectToSimulator())
				        		radioManual.setSelected(false);
				        	else
				        	{
				        		vm.closeInterperter();
				        		new Thread(()->{
					        		vm.joystickMoved();
					        		vm.scrollBarRudderMoved();
					        		vm.scrollBarThrottleMoved();}).start();
				        	}
				        }
				    }
				});
				
				j.setWhenChange(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						new Thread(()->{
						if(radioManual.isSelected())
							vm.joystickMoved();}).start();
					}
				});
				
        		scrollBarLays.valueProperty().addListener((e -> { 
        			new Thread(()->{
        				if(radioManual.isSelected())
        					vm.scrollBarRudderMoved();}).start();
        		}));
        		
        		scrollBarStands.valueProperty().addListener((e -> { 
        			new Thread(()->{
        			if(radioManual.isSelected())
        				vm.scrollBarThrottleMoved();}).start();
        		}));
				

			});
	}
	
	
	//events:
	
	public void btnLoadPressed()
	{
		
		FileChooser fc = new FileChooser();
		 
		fc.setTitle("open map file");
		fc.setInitialDirectory(new File("./"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated Values (*.csv)", "*.csv"));
    
		File chosen = fc.showOpenDialog(stage); //���� ����

		if(chosen!=null)
		{
			vm.calculateMatrix(chosen);
		}
	
		
	}

	public void btnPathPressed()
	{
		
		dialog = new Stage();
		
        dialog.initOwner(stage);
        
        Text txt1 = new Text("Path Calculator Server Details:");
        Text txt2 = new Text("ip: ");
        Text txt3 = new Text("port: ");
        txt1.setFont(new Font("Arial Black", 14));
        txt2.setFont(new Font("Arial Black", 12));
        txt3.setFont(new Font("Arial Black", 12));
        
        TextField txtIp = new TextField();
        TextField txtPort = new TextField();
        txtIp.textProperty().bindBidirectional(vm.getIpPath());
        txtPort.textProperty().bindBidirectional(vm.getPortPath());
        
        HBox h1 = new HBox(10);
        HBox h2 = new HBox(10);
        h1.getChildren().add(txt2);
        h1.getChildren().add(txtIp);
        HBox.setMargin(txtIp,  new Insets(0, 0, 0, 15));
        h2.getChildren().add(txt3);
        h2.getChildren().add(txtPort);
        
        Button b = new Button("Connect");
        b.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
			@Override
			public void handle(MouseEvent event)
			{
				if(!vm.ligellIp(txtIp.getText()))
				{
					return;
				}
				else if(!vm.ligellPort(txtPort.getText()))
				{
					return;
				}
				
				m.setWhenPressed(new EventHandler<MouseEvent>()
				{
					@Override
					public void handle(MouseEvent event)
					{
						if(m.getMap()!=null && m.isPlaneInMap() && m.isXInMap()  &&(!(m.getiPlane().get()==m.getiX().get() && m.getjPlane().get()==m.getjX().get())))
							vm.calculatePath();
						else
						{
							m.setPoints(null);
						}
					}
				});

				if(m.getMap()!=null && m.isPlaneInMap() && m.isXInMap()  &&(!(m.getiPlane().get()==m.getiX().get() && m.getjPlane().get()==m.getjX().get())))
					vm.calculatePath();
				else
				{
					m.setPoints(null);
				}
				
				dialog.hide();
			}
		});
        b.setFont(new Font("Arial Black", 12));
        b.setTextFill(Color.WHITE);
        b.setStyle("-fx-background-color: Black;");
        VBox dialogVbox = new VBox(0);
        
        dialogVbox.getChildren().add(txt1);
        dialogVbox.getChildren().add(h1);
        dialogVbox.getChildren().add(h2);
        dialogVbox.getChildren().add(b);
        
        VBox.setMargin(txt1, new Insets(10, 0, 0, 10));
        VBox.setMargin(h1, new Insets(10, 0, 0, 10));
        VBox.setMargin(h2, new Insets(10, 0, 0, 10));
        VBox.setMargin(b, new Insets(15, 0, 0, 75));

        BorderPane root = new BorderPane(dialogVbox);
        root.setStyle("-fx-background-color: White;");
        
        Scene dialogScene = new Scene(root, 250, 150);
        dialog.setScene(dialogScene);
        dialog.setTitle("Connect To Sumilator Server");
        dialog.setResizable(false);
        
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.show();
	}
	
	public void btnConnectPressed()
	{
		dialog = new Stage();
		
        dialog.initOwner(stage);
        
        Text txt1 = new Text("Simulator Server Details:");
        Text txt2 = new Text("ip: ");
        Text txt3 = new Text("port: ");
        txt1.setFont(new Font("Arial Black", 14));
        txt2.setFont(new Font("Arial Black", 12));
        txt3.setFont(new Font("Arial Black", 12));
        
        TextField txtIp = new TextField();
        TextField txtPort = new TextField();
        txtIp.textProperty().bindBidirectional(vm.getIpSimulator());
        txtPort.textProperty().bindBidirectional(vm.getPortSimulator());
        
        HBox h1 = new HBox(10);
        HBox h2 = new HBox(10);
        h1.getChildren().add(txt2);
        h1.getChildren().add(txtIp);
        HBox.setMargin(txtIp,  new Insets(0, 0, 0, 15));
        h2.getChildren().add(txt3);
        h2.getChildren().add(txtPort);
        
        Button b = new Button("Connect");
        b.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	
			@Override
			public void handle(MouseEvent event) {
				if(vm.ConnectToSimulator())
					dialog.hide();
			}
		});
        b.setFont(new Font("Arial Black", 12));
        b.setTextFill(Color.WHITE);
        b.setStyle("-fx-background-color: Black;");
        VBox dialogVbox = new VBox(0);
        
        dialogVbox.getChildren().add(txt1);
        dialogVbox.getChildren().add(h1);
        dialogVbox.getChildren().add(h2);
        dialogVbox.getChildren().add(b);
        
        VBox.setMargin(txt1, new Insets(10, 0, 0, 10));
        VBox.setMargin(h1, new Insets(10, 0, 0, 10));
        VBox.setMargin(h2, new Insets(10, 0, 0, 10));
        VBox.setMargin(b, new Insets(15, 0, 0, 75));

        BorderPane root = new BorderPane(dialogVbox);
        root.setStyle("-fx-background-color: White;");
        
        Scene dialogScene = new Scene(root, 250, 150);
        dialog.setScene(dialogScene);
        dialog.setTitle("Connect To Sumilator Server");
        dialog.setResizable(false);
        
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.show();
	}
	
	public void btnTextPressed()
	{
		dialog = new Stage();
        dialog.initOwner(stage);

        Text txt1 = new Text("Warning");
        Text txt2 = new Text("this action will replace the current text");
        
        txt1.setFont(new Font("Arial Black", 14));
        txt2.setFont(new Font("Arial Black", 12));

        txt1.setFill(Color.DARKORANGE);
        HBox h = new HBox(10);
        
        Button ok = new Button("ok");
        ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	
			@Override
			public void handle(MouseEvent event) {     
				
				dialog.hide();
				
				FileChooser fc = new FileChooser();
				 
				fc.setTitle("open code file");
				fc.setInitialDirectory(new File("./"));
		        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text (*.txt)", "*.txt"));
		    
				File chosen = fc.showOpenDialog(stage); //���� ����
				
				if(chosen!=null)
				{
					Scanner s;
					try {
						s = new Scanner(chosen);
						StringBuilder sb = new StringBuilder("");
						
						if(s.hasNextLine())
							sb.append(s.nextLine());
						
						while(s.hasNextLine())
						{
							sb.append("\n");
							sb.append(s.nextLine());
						}
						
						s.close();
						txtArea.textProperty().set(sb.toString());
					} catch (FileNotFoundException e) {	vm.fileNotFound();}
					
				}
			}
		});
        ok.setFont(new Font("Arial Black", 12));
        ok.setTextFill(Color.WHITE);
        ok.setStyle("-fx-background-color: Black;");
        
        Button cancel = new Button("cancel");
        cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	
			@Override
			public void handle(MouseEvent event)
			{
				dialog.hide();
			}
        });
        
        cancel.setFont(new Font("Arial Black", 12));
        cancel.setTextFill(Color.WHITE);
        cancel.setStyle("-fx-background-color: Black;");
        
        h.getChildren().add(ok);
        h.getChildren().add(cancel);
        
        HBox.setMargin(cancel,  new Insets(0, 0, 0, 10));
        
        VBox dialogVbox = new VBox(0);
        
        dialogVbox.getChildren().add(txt1);
        dialogVbox.getChildren().add(txt2);
        dialogVbox.getChildren().add(h);
        
        VBox.setMargin(txt1, new Insets(10, 0, 0, 10));
        VBox.setMargin(txt2, new Insets(10, 0, 0, 10));
        VBox.setMargin(h, new Insets(10, 0, 0, 10));

        BorderPane root = new BorderPane(dialogVbox);
        root.setStyle("-fx-background-color: White;");
        
        Scene dialogScene = new Scene(root, 300, 100);
        dialog.setScene(dialogScene);
        dialog.setTitle("Warning");
        dialog.setResizable(false);

		dialog.initModality(Modality.WINDOW_MODAL);
        dialog.show();
	
		
	}

	public void btnSavePressed()
	{
		if(radioAuto.isSelected())
		{
			save();
		}
		else
		{
			dialog = new Stage();
	        dialog.initOwner(stage);

	        Text txt1 = new Text("Warning");
	        Text txt2 = new Text("AutoPilot must be selected");
	        
	        txt1.setFont(new Font("Arial Black", 14));
	        txt2.setFont(new Font("Arial Black", 12));

	        txt1.setFill(Color.DARKORANGE);
	        
	        HBox h = new HBox(10);
	        
	        Button ok = new Button("select autopilot");
	        ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        	
				@Override
				public void handle(MouseEvent event) {     
					
					radioAuto.setSelected(true);
					radioManual.setSelected(false);
					
					
					if(radioAuto.isSelected())
						save();
					
					dialog.hide();
				}
			});
	        ok.setFont(new Font("Arial Black", 12));
	        ok.setTextFill(Color.WHITE);
	        ok.setStyle("-fx-background-color: Black;");
	        
	        Button cancel = new Button("ok");
	        cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        	
				@Override
				public void handle(MouseEvent event)
				{
					dialog.hide();
				}
	        });
	        
	        cancel.setFont(new Font("Arial Black", 12));
	        cancel.setTextFill(Color.WHITE);
	        cancel.setStyle("-fx-background-color: Black;");
	        
	        h.getChildren().add(ok);
	        h.getChildren().add(cancel);
	        
	        HBox.setMargin(cancel,  new Insets(0, 0, 0, 10));
	        
	        VBox dialogVbox = new VBox(0);
	        
	        dialogVbox.getChildren().add(txt1);
	        dialogVbox.getChildren().add(txt2);
	        dialogVbox.getChildren().add(h);
	        
	        VBox.setMargin(txt1, new Insets(10, 0, 0, 10));
	        VBox.setMargin(txt2, new Insets(10, 0, 0, 10));
	        VBox.setMargin(h, new Insets(10, 0, 0, 10));

	        BorderPane root = new BorderPane(dialogVbox);
	        root.setStyle("-fx-background-color: White;");
	        
	        Scene dialogScene = new Scene(root, 300, 100);
	        dialog.setScene(dialogScene);
	        dialog.setTitle("Warning");
	        dialog.setResizable(false);

			dialog.initModality(Modality.WINDOW_MODAL);
	        dialog.show();
			
			
			
			
		}
	}
	
	public void error(Stage stage)
	{
		
		 final Stage dialog = new Stage();
         dialog.initOwner(stage);

         Text txt1 = new Text("ERROR:");
         Text txt2 = new Text();
         txt2.textProperty().bindBidirectional(vm.getError());
         
         txt1.setFont(new Font("Arial Black", 14));
         txt2.setFont(new Font("Arial Black", 12));
 
         txt1.setFill(Color.DARKRED);
         
         VBox dialogVbox = new VBox(0);
         
         dialogVbox.getChildren().add(txt1);
         dialogVbox.getChildren().add(txt2);
         
         VBox.setMargin(txt1, new Insets(10, 0, 0, 10));
         VBox.setMargin(txt2, new Insets(10, 0, 0, 10));

         BorderPane root = new BorderPane(dialogVbox);
         root.setStyle("-fx-background-color: White;");
         
         Scene dialogScene = new Scene(root, 400, 100);
         dialog.setScene(dialogScene);
         dialog.setTitle("ERROR");
         dialog.setResizable(false);

         dialog.initModality(Modality.WINDOW_MODAL);
         dialog.show();
	}
	
	public void save()
	{
		vm.interpret();
	}
	
	private void startPopUp()
	{
		windowNum = 0;
		
		dialog = new Stage();
        dialog.initOwner(stage);
        dialog.setTitle("Guider");
        dialog.setResizable(false);
        dialog.initModality(Modality.WINDOW_MODAL);
        
        Scene scene = GetWindow(windowNum);
        if(scene!=null)
        {
        	dialog.setScene(GetWindow(windowNum));
        	dialog.show();
        }
		


	}
	
	private Scene GetWindow(int num)
	{
		Scanner s;
		try {
			s = new Scanner(new FileInputStream(new File("./resources/instruction to the project.txt")));
		} catch (FileNotFoundException e) {
			return null;
		}
		for(int i=0;i<windowNum;i++)
		{
			while(s.hasNextLine())
			{
				if(s.nextLine().equals("========================================================================================================================"))
					break;
			}
		}
		
		VBox dialogVbox = new VBox(0);
		
		int typeOfMe, typeOfPrev = -1;
		
		while(s.hasNextLine())
		{
			String line = s.nextLine();
			if(line.equals("========================================================================================================================"))
				break;
			
			Text txt = new Text();
			if (line.startsWith("_"))
			{
				txt.setFont(new Font("Arial Black", 14));
				line = line.substring(1);
				typeOfMe = 0;
			}
			else if (line.startsWith("*"))
			{
				txt.setFont(new Font("Arial Black", 12));
				line = line.substring(1);
				typeOfMe = 1;
			}
			else
			{
				txt.setFont(new Font("Arial", 12));
				typeOfMe = 2;
			}
			
			txt.setText(line);
			dialogVbox.getChildren().add(txt);
			
			int margin = 10;
			if(typeOfMe == 2 && typeOfPrev == 2)
				margin = 0;
			
			VBox.setMargin(txt, new Insets(margin, 0, 0, 10));
			
			typeOfPrev = typeOfMe;
		}
		
		s.close();
		
		  HBox h = new HBox(10);
	    
	      Button prev = new Button("previous");
	      if(windowNum ==0)
	      {
	    	  prev.setDisable(true);
		      prev.setStyle("-fx-background-color: DarkGray;");
	      }
	      else
	      {
	    	  prev.setDisable(false);
		      prev.setStyle("-fx-background-color: Black;");
	      }
	      prev.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        	
				@Override
				public void handle(MouseEvent event) {     
					windowNum--;
					Scene scene = GetWindow(windowNum);
					if(scene!=null)
					{
						dialog.setScene(GetWindow(windowNum));
				        dialog.show();
					}
					else
						dialog.hide();
				}
			});
	        prev.setFont(new Font("Arial Black", 12));
	        prev.setTextFill(Color.WHITE);
	      
	      Button next = new Button("next");
	      if(windowNum ==5)
	      {
	    	  next.setDisable(true);
		      next.setStyle("-fx-background-color: DarkGray;");
	      }
	      else
	      {
	    	  next.setDisable(false);
		      next.setStyle("-fx-background-color: Black;");
	      }
	      next.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        	
				@Override
				public void handle(MouseEvent event) {     
					windowNum++;
					Scene scene = GetWindow(windowNum);
					if(scene!=null)
					{
						dialog.setScene(GetWindow(windowNum));
				        dialog.show();
					}
					else
						dialog.hide();
				}
			});
	      next.setFont(new Font("Arial Black", 12));
	      next.setTextFill(Color.WHITE);
	      
	      Button fin = new Button("finish");
	      fin.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        	
				@Override
				public void handle(MouseEvent event) {     
					dialog.hide();
				}
			});
	      fin.setFont(new Font("Arial Black", 12));
	      fin.setTextFill(Color.WHITE);
	      fin.setStyle("-fx-background-color: Black;");
	      
	      h.getChildren().add(prev);
	      h.getChildren().add(next);
	      h.getChildren().add(fin);
	      
	      BorderPane root = new BorderPane(dialogVbox, null, null, h, null);
	      BorderPane.setMargin(h, new Insets(0,0,10,10));
	      root.setStyle("-fx-background-color: White;");
	        
	      return new Scene(root, 600, 410);	
	}

}
