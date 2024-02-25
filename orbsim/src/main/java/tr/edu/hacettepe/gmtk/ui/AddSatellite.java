package tr.edu.hacettepe.gmtk.ui;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.orekit.propagation.analytical.tle.TLE;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tr.edu.hacettepe.gmtk.MainGui;

public class AddSatellite{
	
	
	private MainGui gui;


	public AddSatellite(MainGui gui) {
		this.gui = gui;
	}
	
	public void display() {
	
		Stage addSatWindow = new Stage();
		addSatWindow.setTitle("Add Satellite");
		
		ToggleGroup group = new ToggleGroup();
		
		GridPane addGrid = new GridPane();
		
		RadioButton satRadio = new RadioButton("Satellite");
		satRadio.setToggleGroup(group);
		satRadio.setSelected(true);
		GridPane.setConstraints(satRadio, 0, 0);
		
		RadioButton remoteSatRadio = new RadioButton("Remote Sensing Satellite");
		remoteSatRadio.setToggleGroup(group);
		GridPane.setConstraints(remoteSatRadio, 1, 0);
		
		Label nameLabel = new Label("Satellite Name: ");
		GridPane.setConstraints(nameLabel, 0, 3);
		Label tle1 = new Label("First TLE Row: ");
		GridPane.setConstraints(tle1, 0, 4);
		Label tle2 = new Label("Second TLE Row: ");
		GridPane.setConstraints(tle2, 0, 5);
		
		TextField nameField = new TextField("ISS");
		GridPane.setConstraints(nameField, 1, 3);
		TextField tleField1 = new TextField("1 25544U 98067A   23098.36644676  .00015955  00000+0  29076-3 0  9995");
		tleField1.setMinWidth(300);
		GridPane.setConstraints(tleField1, 1, 4);
		TextField tleField2 = new TextField("2 25544  51.6405 314.5162 0006500 168.0373 198.0529 15.49610007390968");
		GridPane.setConstraints(tleField2, 1, 5);
		
		Label focalLen = new Label("Focal Length: ");
        GridPane.setConstraints(focalLen,0,7);
        Label sensorWidth = new Label("Sensor Width: ");
        GridPane.setConstraints(sensorWidth,0,8);
        
        TextField focalLenText = new TextField();
		GridPane.setConstraints(focalLenText, 1, 7);
		focalLenText.promptTextProperty().set("mm");
		TextField sensorWidthText = new TextField();
		GridPane.setConstraints(sensorWidthText, 1, 8);
		sensorWidthText.promptTextProperty().set("mm");
		
		
		Button addSatList = new Button("ADD SPACE STATION");
		GridPane.setConstraints(addSatList,1,10); 
		addSatList.setOnAction(e ->{
			try {
				gui.addSatelliteFromWeb();
			} catch (IOException | XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			addSatWindow.close();
		});
		
		Button addButton = new Button("ADD SATELLITE");
		addButton.setOnAction(e -> {
			
			if(focalLenText.getText().isEmpty() == true && sensorWidthText.getText().isEmpty() == true) {
				Satellite sat = new Satellite(nameField.getText(), new TLE(tleField1.getText(), tleField2.getText()));
				try {
					gui.addSatellite(sat);
				} catch (IOException | XMLStreamException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else {
				double focalLength = Double.parseDouble(focalLenText.getText());
				double sensorWidth2 = Double.parseDouble(sensorWidthText.getText());
				
				Satellite sat = new Satellite(nameField.getText(), new TLE(tleField1.getText(), tleField2.getText()),focalLength,sensorWidth2);
				try {
					gui.addSatellite(sat);
				} catch (IOException | XMLStreamException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			addSatWindow.close();	
		});
		GridPane.setConstraints(addButton, 1, 9);
		
		

		
		group.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
			if (new_toggle != null) {
                RadioButton selected = (RadioButton) new_toggle;
                if (selected == remoteSatRadio) {
                    addGrid.getChildren().addAll(focalLen, focalLenText, sensorWidth, sensorWidthText);
                } else if (selected == satRadio) {
                    addGrid.getChildren().removeAll(focalLen, focalLenText, sensorWidth, sensorWidthText);
                }
            }
        });
		
		addGrid.getChildren().addAll(satRadio, remoteSatRadio, nameLabel, tle1, tle2,
				nameField, tleField1, tleField2, addButton, addSatList);
		addGrid.setPadding(new Insets(10,10,10,10)); 
		addGrid.setVgap(8); 
		addGrid.setHgap(10);
		addGrid.setStyle("-fx-border-color: blue;"
                + "-fx-border-width: 2 2 2 2;");
		
		Scene scene = new Scene(addGrid,500,320);
		scene.getStylesheets().add("Background.css");
		addSatWindow.setScene(scene);
		addSatWindow.setResizable(false);
		addSatWindow.initModality(Modality.APPLICATION_MODAL);
		addSatWindow.showAndWait();
		
	}

}
