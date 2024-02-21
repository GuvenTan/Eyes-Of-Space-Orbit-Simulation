package tr.edu.hacettepe.gmtk;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.orekit.data.ClasspathCrawler;
import org.orekit.data.DataContext;
import org.orekit.time.AbsoluteDate;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Angle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tr.edu.hacettepe.gmtk.ui.AddSatellite;
import tr.edu.hacettepe.gmtk.ui.OrbitSimulator;
import tr.edu.hacettepe.gmtk.ui.Satellite;
import tr.edu.hacettepe.gmtk.ui.TLECommon;



public class MainGui extends Application{
	
	public ComboBox<Satellite> satList;
	int seconds = 0;
	AnchorPane AncPane;
	
	OrbitSimulator simulator = null;
	private TextField timeField;
	private TextField mjdField;
	private WorldWindowGLJPanel wwd;
	public RadioButton nadirViewRadioItem;
	public RadioButton threeDViewRadioItem;
	public ToggleGroup group;
	private double i = 1;

    
	public static void main(String[] args) {
		DataContext.getDefault().getDataProvidersManager().addProvider(new ClasspathCrawler("orekit-data.zip"));		
        launch(args);
        
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        
    	wwd = new WorldWindowGLJPanel();
        wwd.setModel(new BasicModel());
        
        
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(wwd);
        swingNode.setVisible(true);
        
        simulator = new OrbitSimulator(this);
        
        Label labelDate = new Label("Date: ");
        Label labelJulian = new Label("Julian: ");
        Label labelAddSatellite = new Label("Add Satellite");
        Label label1 = new Label("                 					        	");
        
        Label speedLabel = new Label(i + "x");
        speedLabel.setStyle("-fx-font-size: 12px;");
        
        
        Label labelEmpt1 = new Label(" ");
        Label labelEmpt2 = new Label(" ");
        Label labelEmpt3 = new Label(" ");
        Label labelEmpt4 = new Label(" ");
        Label labelEmpt5 = new Label(" ");
        //Label labelEmpt6 = new Label(" ");
        
        Label labelInfo = new Label();
        labelInfo.setStyle("-fx-text-fill: #ff2400;");
        Label labelSatName = new Label();
        Label labelI = new Label();
        Label labelE = new Label();
        Label labelRaan = new Label();
        Label labelPerigeeArg = new Label();
        Label labelSemiMajor = new Label();
        
        satList = new ComboBox<Satellite>();
        satList.setItems(simulator.getAllSatellites());
        satList.getItems().add(null);
        satList.setOnAction(e-> {
        	Satellite satName = satList.getSelectionModel().getSelectedItem();
        	labelInfo.setText("Satellite Information");
        	labelSatName.setText("Satellite Name: " + satName.getName());
        	labelI.setText("Inclenation: " + Angle.fromDegrees(satName.getTle().getI()));
        	labelE.setText("Eccentricity: " + satName.getTle().getE());
        	labelRaan.setText("RAAN: " + Angle.fromDegrees(satName.getTle().getRaan()));
        	labelPerigeeArg.setText("Perigee Argument: " + Angle.fromDegrees(satName.getTle().getPerigeeArgument()));
        	labelSemiMajor.setText("Semi Major Axis: " + getSemiMajor(satName.getTle().getMeanMotion()));
        });
       
        satList.setMinWidth(180);
        
        //FileInputStream slowInputStream = new FileInputStream("C:\\eclipse-workspace\\OrbitSimulation\\src\\main\\resources\\slowDown.png");
        Image slowIcon = new Image(getClass().getClassLoader().getResourceAsStream("slowDown.png"));
        ImageView slowDownView = new ImageView(slowIcon);
        slowDownView.setFitWidth(20);
        slowDownView.setFitHeight(20);
        Button slowDownButton = new Button("", slowDownView);
        slowDownButton.setPrefSize(20,20);
        slowDownButton.setOnAction(e->{
        	simulator.setTimeIncrement(simulator.getTimeIncrement()/2.0);
        	setTimeIndex2(2);
        	speedLabel.setText(getTimeIndex() + "x");
        });
        
        //FileInputStream playInputStream = new FileInputStream("C:\\eclipse-workspace\\OrbitSimulation\\src\\main\\resources\\play.png");
        Image playIcon = new Image(getClass().getClassLoader().getResourceAsStream("play.png"));
        ImageView playIconView = new ImageView(playIcon);
        playIconView.setFitWidth(20);
        playIconView.setFitHeight(20);
        Button playButton = new Button("", playIconView);
        playButton.setPrefSize(20, 20);
        
        playButton.setOnAction(e->{
        	if (simulator.isRunning()) {
        		simulator.stopAnimation();
        	} else {
        		simulator.startAnimation();
        	}
        	simulator.toNow();
        });
        
        //FileInputStream speedInputStream = new FileInputStream("C:\\eclipse-workspace\\OrbitSimulation\\src\\main\\resources\\speedUp.png");
        Image speedIcon = new Image(getClass().getClassLoader().getResourceAsStream("speedUp.png"));
        ImageView speedIconView = new ImageView(speedIcon);
        speedIconView.setFitWidth(20);
        speedIconView.setFitHeight(20);
        Button speedUpButton = new Button("",speedIconView);
        speedUpButton.setPrefSize(20, 20);
        
        speedUpButton.setOnAction(e->{
        	simulator.setTimeIncrement(simulator.getTimeIncrement()*2);
        	setTimeIndex1(2);
        	speedLabel.setText(getTimeIndex() + "x");
        });
        
        
        Button nowButton = new Button("Now");
        nowButton.setOnAction(e-> {
        	simulator.toNow();
        	
        });
        
        //FileInputStream addSatInputStream = new FileInputStream("C:\\eclipse-workspace\\OrbitSimulation\\src\\main\\resources\\plus.png");
        Image addSatIcon = new Image(getClass().getClassLoader().getResourceAsStream("plus.png"));
        ImageView addSatIconView = new ImageView(addSatIcon);
        addSatIconView.setFitWidth(20);
        addSatIconView.setFitHeight(20);
        Button addSatButton = new Button("",addSatIconView);
        addSatButton.setPrefSize(20, 20);
        
        addSatButton.setOnAction(e -> {
        	AddSatellite aSat = new AddSatellite(this);
        	aSat.display();	
        });
        
        Button removeSatelliteButton = new Button("Clear");
        removeSatelliteButton.setOnAction(e -> removeSatellite());
        
        timeField = new TextField();
        timeField.setMinWidth(200);
        mjdField = new TextField("Julian Date: ");
        
        HBox controlPanel = new HBox();
        controlPanel.setPadding(new Insets(10, 10, 10, 10));
        controlPanel.setSpacing(10);
        controlPanel.getChildren().addAll(satList, slowDownButton, playButton,
        		speedUpButton, speedLabel ,labelDate ,timeField, nowButton, labelJulian, mjdField, label1,labelAddSatellite,addSatButton,removeSatelliteButton);
        controlPanel.setStyle("-fx-border-color: blue;"
                + "-fx-border-width: 2 2 2 2;");
        
        group = new ToggleGroup();
        
        threeDViewRadioItem = new RadioButton("3D View");
        threeDViewRadioItem.setToggleGroup(group);
        threeDViewRadioItem.setSelected(true);
        
        nadirViewRadioItem = new RadioButton("Nadir View"); 
        nadirViewRadioItem.setToggleGroup(group);
        
        VBox controlPanelV = new VBox();
        controlPanelV.setPadding(new Insets(10,100,10,10));       
        controlPanelV.getChildren().addAll(threeDViewRadioItem, nadirViewRadioItem,labelEmpt1,labelEmpt2,labelEmpt3,labelEmpt4,labelEmpt5,
        		labelInfo, labelSatName, labelSemiMajor, labelI, labelE, labelPerigeeArg, labelRaan);
        
        VBox.setMargin(threeDViewRadioItem, new Insets(10,10,10,10));
        VBox.setMargin(nadirViewRadioItem, new Insets(10,10,10,10));
        controlPanelV.setStyle("-fx-border-color: blue;"
                + "-fx-border-width: 2 2 2 2;");
        
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(swingNode);
        borderPane.setTop(controlPanel);
        borderPane.setLeft(controlPanelV);
        
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        
        Scene scene = new Scene(borderPane,screenWidth,screenHeight);
        scene.getStylesheets().add("Background.css");
        //stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("EOSOS"); 
        stage.setScene(scene);
        //stage.setFullScreen(true);
        stage.show();
        
    }

	public void updateDate(AbsoluteDate date) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				timeField.setText(date.toString(TLECommon.UTC) + " UTC");
				
				mjdField.setText(String.valueOf(date.getComponents(TLECommon.UTC).getDate().getJ2000Day()));
			}
		});
		
	}

	public WorldWindow getWorldWindow() {
		return wwd;
	}

	public void addSatellite(Satellite sat) throws IOException, XMLStreamException {
		simulator.addSatellite(sat);
	}
	
	public void removeSatellite() {
		simulator.removeSatellite();
	}
	
	public void addSatelliteFromWeb() throws IOException, XMLStreamException {
		simulator.addSatFromWeb();
	}

	public double getSemiMajor(double nu) {
		double n = Math.pow(nu, 2);
		double GM = 3.986004418 * Math.pow(10, 14);
		double c =  GM / n;
		double a = Math.cbrt(c);
		double a_km = a / 1000;
		return a_km;
	}
	
	public double getTimeIndex() {
		return i;
	}
	
	public void setTimeIndex1(double x) {
		i = i * x;
	}
	
	public void setTimeIndex2(double x) {
		i = i / x;
	}
	
	
}
