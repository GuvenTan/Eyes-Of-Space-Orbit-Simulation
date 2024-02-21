package tr.edu.hacettepe.gmtk.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.orekit.propagation.analytical.tle.TLE;

public class GetSatelliteAuto{
	
	public URL url;
    public ArrayList<Satellite> satellites = new ArrayList<>();
    private String line;

    
    
	
	public GetSatelliteAuto() {
		super();
	}



	public void readURL() throws IOException {
	    url = new URL("https://celestrak.org/NORAD/elements/gp.php?GROUP=stations&FORMAT=tle");
	    URLConnection con = url.openConnection();
	    BufferedReader reader;
	    try {
	    	reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} catch (IOException e) {
			System.out.println("IOException occurred while opening URL connection.");
		    throw e;
	    }
	    
	    while ((line = reader.readLine()) != null) {
            String name = line;
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            TLE tle = new TLE(line1, line2);
            Satellite satellite = new Satellite(name, tle);
            satellites.add(satellite);
            
        }
	}



	public ArrayList<Satellite> getSatellites() {
		return satellites;
	}



	public void setSatellites(ArrayList<Satellite> satellites) {
		this.satellites = satellites;
	}
	
	
	
	
}

