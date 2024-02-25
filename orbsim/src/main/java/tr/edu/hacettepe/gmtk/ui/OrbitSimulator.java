package tr.edu.hacettepe.gmtk.ui;

import java.awt.Color;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.stream.XMLStreamException;
import org.hipparchus.geometry.euclidean.threed.Line;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.data.DataContext;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.UTCScale;
import org.orekit.utils.TimeStampedPVCoordinates;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.Polygon;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tr.edu.hacettepe.gmtk.MainGui;

public class OrbitSimulator {

	private final UTCScale UTC = DataContext.getDefault().getTimeScales().getUTC();

	// To WorldWind Objects
	public Hashtable<String, WWSatellite> wwSatellites = new Hashtable<>();
	
	// To Combobox
	private ObservableList<Satellite> allSatellites = FXCollections.observableArrayList();
	
	private ArrayList<Position> pathPositions = new ArrayList<>();

	private AbsoluteDate date;
	private WorldWindow wwd;
	private Timer timer;
	private double timeIncrement = 0.1;
	private MainGui controller;
	private Angle angle;

	//BMNGWMSLayer satelliteLayer = new BMNGWMSLayer();
	
	RenderableLayer satellites = new RenderableLayer();
	
	public OrbitSimulator(MainGui gui) {
		toNow();
		this.controller = gui;
		this.wwd = gui.getWorldWindow();
		//this.wwd.getModel().getLayers().clear();
		this.wwd.getModel().getLayers().add(satellites);
		//this.wwd.getModel().getLayers().add(satelliteLayer);
		
	}
	
	
	public void setTimeIncrement(double seconds) {
		this.timeIncrement = seconds;
	}
	
	
	public ObservableList<Satellite> getAllSatellites() {
		return allSatellites;
	}
	
	public void addSatellite(Satellite sat) throws IOException, XMLStreamException {
		WWSatellite wwSat = new WWSatellite(getWwd(), sat,date);
		wwSatellites.put(sat.getName(), wwSat);
		satellites.addRenderable(wwSat.getModel());
		allSatellites.add(sat);
	}
	
	public void addSatFromWeb() throws IOException, XMLStreamException {
		GetSatelliteAuto getSat = new GetSatelliteAuto();
		getSat.readURL();
		for(Satellite i: getSat.satellites) {
			WWSatellite wwSat = new WWSatellite(getWwd(), i,date);
			wwSatellites.put(i.getName(), wwSat);
			satellites.addRenderable(wwSat.getModel());
			allSatellites.add(i);
		}
		
	}
	
	public void removeSatellite() {
		satellites.removeAllRenderables();
		allSatellites.clear();
	}
	
	
	public void toNow() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        this.date= new AbsoluteDate(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond(), UTC);
	}
	
	
	public void incrementTime(double seconds) {
		this.date = this.date.shiftedBy(seconds);
		controller.updateDate(date);
		
		if (controller.satList.getValue() != null) {
			
			toCameraPos();
			updateSatelliteStates();
			drawOrbit();
			
		}else if(controller.satList.getValue() == null){
			updateSatelliteStates();
			clearOrbit();
		}
	}
	
	private void updateSatelliteStates() {
		for (String k:wwSatellites.keySet()) {
			wwSatellites.get(k).updatePosition(wwd, date);
		}
		
	}
	
	private void toCameraPos() {
		/////// Uydu türüne göre nadir buton aktif veya değil çalışma yap.
		//if() {
		if (controller.group.getSelectedToggle() == controller.nadirViewRadioItem){
			getNadirCamera();
			
		}else if (controller.group.getSelectedToggle() == controller.threeDViewRadioItem) {
			focusSelectedSatellite();
			drawNadirArea();
		}
		//}
	}
	
	public void getNadirCamera() {
		
		wwd.getView().setEyePosition(wwSatellites.get(controller.satList.getValue().getName()).getCurrentPosition());
		wwd.getView().setFieldOfView(Angle.fromRadians(controller.satList.getValue().calculateFOV()));
		wwd.getView().setHeading(angle);

	}
	
	
	// listeden seçilen uyduya odaklanma.
	private void focusSelectedSatellite() {	
		BasicOrbitView orbit = new BasicOrbitView();
		orbit.setCenterPosition(wwSatellites.get(controller.satList.getValue().getName()).getCurrentPosition());
		wwd.setView(orbit);
	}
	
	private void drawOrbit() {
	
		
		pathPositions.add((wwSatellites.get(controller.satList.getValue().getName())).getCurrentPosition());
		
		ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setOutlineMaterial(new Material(Color.RED));
        attrs.setOutlineWidth(2d);
       
		
		Path path = new Path(pathPositions);
        path.setAttributes(attrs);
        path.setVisible(true);
        path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        path.setPathType(AVKey.GREAT_CIRCLE);
        satellites.addRenderable(path);
	}
	
	private void clearOrbit() {
		pathPositions.clear();
		
	}
	
	private void drawNadirArea() {
		
		ArrayList<Position> pgonPoints = new ArrayList<Position>();
		
		TLE tle = controller.satList.getValue().getTle();
		TimeStampedPVCoordinates pvt = TLECommon.getPropagator(tle).propagate(date).getPVCoordinates(TLECommon.ITRF);
		
		Vector3D RealPos = pvt.getPosition();
        Vector3D pos = pvt.getPosition().normalize();  // get norm vectors
        Vector3D vel = pvt.getVelocity().normalize();
        
        double focalLen = controller.satList.getValue().getFocalLen()/1000; // meter
        //System.out.println(vel.getX());
        //System.out.println(vel.getY());
        //System.out.println(vel.getZ());
        
        //System.out.println(pos.getX());
        //System.out.println(pos.getY());
        //System.out.println(pos.getZ());
        

        double[] konum = {pos.getX(), pos.getY(), pos.getZ()};
        double[] hız = {vel.getX(),vel.getY(),vel.getZ()};
        
        double[] aVec = new double[konum.length];
        double[] bVec = new double[hız.length];
        double[] cVec = new double[hız.length];
        

        for(int i = 0; i < konum.length; i++) {
        	double axis = konum[i] * focalLen;
        	aVec[i] = axis;
        }
        
        
        double sumOfSquares = 0;
        for (int i = 0; i < aVec.length; i++) {
        	sumOfSquares += Math.pow(aVec[i], 2);
        }
        
        double sum = Math.sqrt(sumOfSquares);
        
        double fov = controller.satList.getValue().calculateFOV();
        
        double bLen = sum * Math.tan(fov/2);
        
        for (int i = 0; i < hız.length; i++) {
        	double b = hız[i] * bLen;
        	bVec[i] = b;
        }
        
        Vector3D konumVec = new Vector3D(konum[0],konum[1],konum[2]); 
        
        Vector3D hızVec = new Vector3D(hız[0],hız[1],hız[2]);
        
        Vector3D yVec = konumVec.crossProduct(hızVec);
        
        double[] y = {yVec.getX(), yVec.getY(), yVec.getZ()};
        
        for(int i = 0; i < cVec.length; i++) {
        	double c = y[i]*bLen;
        	cVec[i] = c;
        }
        
        Vector3D aVector = new Vector3D(aVec[0], aVec[1], aVec[2]);
        Vector3D bVector = new Vector3D(bVec[0], bVec[1], bVec[2]);
        Vector3D cVector = new Vector3D(cVec[0], cVec[1], cVec[2]); 
      
        Vector3D cross1 = RealPos.subtract(aVector).add(bVector).add(cVector);
        Vector3D cross2 = RealPos.subtract(aVector).subtract(bVector).add(cVector);
        Vector3D cross3 = RealPos.subtract(aVector).subtract(bVector).subtract(cVector);
        Vector3D cross4 = RealPos.subtract(aVector).add(bVector).subtract(cVector);

        Line line1 = new Line(RealPos, cross1, 1);
        Line line2 = new Line(RealPos, cross2, 1);
        Line line3 = new Line(RealPos, cross3, 1);
        Line line4 = new Line(RealPos, cross4, 1);
		
        GeodeticPoint intersection1 = TLECommon.EARTH.getIntersectionPoint(line1, cross1, TLECommon.ITRF, date);
        GeodeticPoint intersection2 = TLECommon.EARTH.getIntersectionPoint(line2, cross2, TLECommon.ITRF, date);
        GeodeticPoint intersection3 = TLECommon.EARTH.getIntersectionPoint(line3, cross3, TLECommon.ITRF, date);
        GeodeticPoint intersection4 = TLECommon.EARTH.getIntersectionPoint(line4, cross4, TLECommon.ITRF, date);
        
		
        pgonPoints.add(Position.fromRadians(intersection1.getLatitude(), intersection1.getLongitude(), 10000));
        pgonPoints.add(Position.fromRadians(intersection2.getLatitude(), intersection2.getLongitude(), 10000));
        pgonPoints.add(Position.fromRadians(intersection3.getLatitude(), intersection3.getLongitude(), 10000));
        pgonPoints.add(Position.fromRadians(intersection4.getLatitude(), intersection4.getLongitude(), 10000));
        
        Polygon pgon = new Polygon(pgonPoints);
        
        pgon.setAttributes(new BasicShapeAttributes() {{
            setDrawOutline(true);
            setOutlineMaterial(Material.RED);
            setInteriorMaterial(Material.RED);
	        setInteriorOpacity(0.1);
	        }});
        
        

		angle = LatLon.linearAzimuth(Position.fromRadians(intersection1.getLatitude(), intersection1.getLongitude(),10000),Position.fromRadians(intersection2.getLatitude(), intersection2.getLongitude(),10000));
		satellites.addRenderable(pgon);
                
	}
	
	public void startAnimation() {
		this.timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				incrementTime(timeIncrement);
				getWwd().redraw();
			}
		}, 0, 100);
	}
	
	public void stopAnimation() {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
	}


	public WorldWindow getWwd() {
		return wwd;
	}


	public void setWwd(WorldWindow wwd) {
		this.wwd = wwd;
	}


	public boolean isRunning() {
		return timer != null;
	}


	public double getTimeIncrement() {
		return timeIncrement;
	}
	
}
