package tr.edu.hacettepe.gmtk.ui;


import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataContext;
import org.orekit.frames.Frame;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;

import org.orekit.time.UTCScale;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

public class TLECommon {
	public static final UTCScale UTC = DataContext.getDefault().getTimeScales().getUTC();
	

	
	public static Frame ITRF  = DataContext.getDefault().getFrames().getITRF(IERSConventions.IERS_2010, false);
	
	public static OneAxisEllipsoid EARTH =  new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, 
			                         Constants.WGS84_EARTH_FLATTENING, 
			                         ITRF);
	
	public static Frame GCRF = DataContext.getDefault().getFrames().getGCRF();
	
	public static TLEPropagator getPropagator(TLE tle) {
		return TLEPropagator.selectExtrapolator(tle);		
	}
}
