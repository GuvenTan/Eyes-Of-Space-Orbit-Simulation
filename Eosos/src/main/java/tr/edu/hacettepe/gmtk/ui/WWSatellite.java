package tr.edu.hacettepe.gmtk.ui;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.orekit.time.AbsoluteDate;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.ogc.collada.ColladaRoot;
import gov.nasa.worldwind.ogc.collada.impl.ColladaController;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;

public class WWSatellite {

	
	private Satellite sat;
	private ColladaRoot satModel;
	ColladaController colladaController;

	public WWSatellite(WorldWindow wwd, Satellite sat,AbsoluteDate date) throws IOException, XMLStreamException {
		this.sat= sat;
        
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(Material.YELLOW);
        attrs.setInteriorOpacity(1);
        attrs.setEnableLighting(true);
        attrs.setOutlineMaterial(Material.RED);
        attrs.setOutlineWidth(2d);
        attrs.setDrawInterior(true);
        attrs.setDrawOutline(true);

        
        Vec4 vec2 = new Vec4(1500,1500,1500);
        satModel = ColladaRoot.createAndParse(getClass().getClassLoader().getResourceAsStream("duck_triangulate.dae"));
        satModel.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        colladaController = new ColladaController(satModel);
        satModel.setModelScale(vec2);
	}

	private Position calculateSatellitePosition(WorldWindow wwd, AbsoluteDate date) {
		Position pos = sat.getPosition(date);
		return pos;
	}

	public void updatePosition(WorldWindow wwd, AbsoluteDate date) {
		this.satModel.setPosition(calculateSatellitePosition(wwd, date));
		this.satModel.setHeading(Angle.fromDegrees(calculateSatDirection(date)));
	}
	public Position getCurrentPosition() {
		return satModel.getPosition();
	}
	
	public double calculateSatDirection(AbsoluteDate date) {
		double direct = sat.getDirection(date);
		return direct;
	}

	public ColladaController getModel() {
		return colladaController;
	}
}
