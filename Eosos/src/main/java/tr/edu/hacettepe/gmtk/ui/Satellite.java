package tr.edu.hacettepe.gmtk.ui;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.TimeStampedPVCoordinates;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;


public class Satellite {

	private String name;
	
	private TLE tle;

	private TLEPropagator propagator;
	
	private double focalLength; // mm
	
	private double sensorWidth; // km

	public Satellite(String name, TLE tle) {
		this.name = name;
		this.tle = tle;
		this.propagator = TLECommon.getPropagator(tle);
		
	}
	public Satellite(String name, TLE tle, double string, double string2) {
		this.name = name;
		this.tle = tle;
		this.focalLength = string;
		this.sensorWidth = string2;
		this.propagator = TLECommon.getPropagator(tle);
	}
	
	public String getName() {
		return name;
	}
	
	public double calculateFOV() {
		double fov = 2 * Math.atan(this.sensorWidth / (2 * this.focalLength));
		return fov;
	}
	
	public TLE getTle() {
		return tle;
	}

	public Position getPosition(AbsoluteDate date) {
		TimeStampedPVCoordinates pvt = propagator.propagate(date).getPVCoordinates(TLECommon.ITRF);
        Vector3D pos = pvt.getPosition();
        GeodeticPoint point = TLECommon.EARTH.transform(pos, TLECommon.ITRF, date);

        LatLon posDegree = Position.fromRadians(point.getLatitude(), point.getLongitude(), point.getAltitude());
      
        double[] latLonAlt = posDegree.asDegreesArray();
        
        Position newPos = Position.fromDegrees(latLonAlt[0],latLonAlt[1],point.getAltitude());
       
		return newPos;
	}
	
	public double getDirection(AbsoluteDate date) {
		
		TimeStampedPVCoordinates pvt = propagator.propagate(date).getPVCoordinates(TLECommon.ITRF);
		
		Vector3D direction = pvt.getVelocity().normalize();
		
		Vector3D itrfNorth = new Vector3D(0,0,1);

		double angle = Math.acos(direction.dotProduct(itrfNorth) / (direction.getNorm() * itrfNorth.getNorm()));
		
		double degree = Math.toDegrees(angle);
		
		return degree;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean getFocalLength() {
		return true;
	}
	
	public double getFocalLen() {
		return focalLength;
	}
	
}
