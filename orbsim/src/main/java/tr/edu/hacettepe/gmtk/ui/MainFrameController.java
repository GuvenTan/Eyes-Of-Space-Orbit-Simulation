package tr.edu.hacettepe.gmtk.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;

import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.layers.RenderableLayer;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class MainFrameController implements Initializable{


	@FXML AnchorPane wwAnchor;
	private SwingNode wwNode;
	private WorldWindowGLJPanel wwd;
	private OrbitSimulator orbitSimulator;
	@FXML TextField dateTime;
	@FXML ListView<Satellite> lstSat;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		
		initializeWorldWind();

		
		AnchorPane.setTopAnchor(wwNode, 0d);
		AnchorPane.setBottomAnchor(wwNode, 0d);
		AnchorPane.setRightAnchor(wwNode, 0d);
		AnchorPane.setLeftAnchor(wwNode, 0d);  		
		wwAnchor.getChildren().add(wwNode);
		
		this.orbitSimulator = new OrbitSimulator(null);
				
	}
	
	private void initializeWorldWind() {
        wwNode = new SwingNode();

        SwingUtilities.invokeLater(() -> {
            wwd = new WorldWindowGLJPanel();
            Model m = (Model)WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            wwd.setModel(m);
            
            RenderableLayer sats = new RenderableLayer();
            sats.setName("Sats");
            wwd.getModel().getLayers().add(sats);
            
            
            wwNode.setContent(wwd);
            orbitSimulator.setWwd(wwd);
        });
	}

	@FXML public void onStart() throws IOException, XMLStreamException {
		
		TLE tle = new TLE("1 25544U 98067A   23064.88621528  .00022820  00000-0  41046-3 0  9997", "2 25544  51.6409 120.2328 0005881  59.8810 276.3963 15.49724096385760");
		Satellite sat = new Satellite("ISS", tle);
		orbitSimulator.addSatellite(sat);
		
		lstSat.getItems().add(sat);
		
		
		orbitSimulator.startAnimation();
		
	}

	@FXML public void onStop() {
		
		
		
		
	}

	public void updateDate(AbsoluteDate date) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				dateTime.setText(date.toString(TLECommon.UTC));
			}
		});
	}

}
