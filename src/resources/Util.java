package resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.util.ResourceLoader;

public class Util {

	private static HashMap<String, Image> images = new HashMap<String, Image>();
	private static HashMap<String, ConfigurableEmitter> xml = new HashMap<String, ConfigurableEmitter>();
	
	public static void addObject(String s, String key) throws SlickException, IOException {
		
		String data = s.substring(s.length() - 3);
		System.out.println("DataType: " + data + " || String: " + s);
		
		if(data.equals("png")) {
			images.put(key, new Image(ResourceLoader.getResourceAsStream(s), s, false));
		} else if(data.equals("xml")) {
			InputStream in = ResourceLoader.getResourceAsStream(s);
			xml.put(key, ParticleIO.loadEmitter(in));
		}
		
	}
	
	public static Image getImage(String key) {
		return images.get(key);
	}
	public static ConfigurableEmitter getEmitter(String key) {
		return xml.get(key);
	}
	
	public static void removeImage(String key) {
		images.remove(key);
	}
	public static void removeXML(String key) {
		xml.remove(key);
	}

	public static double getAngleFromPoint(Point firstPoint, Point secondPoint) {

	    if((secondPoint.getX() > firstPoint.getX())) {//above 0 to 180 degrees

	        return (Math.atan2((secondPoint.getX() - firstPoint.getX()), (firstPoint.getY() - secondPoint.getY())) * 180 / Math.PI);

	    }
	    else if((secondPoint.getX() < firstPoint.getX())) {//above 180 degrees to 360/0

	        return 360 - (Math.atan2((firstPoint.getX() - secondPoint.getX()), (firstPoint.getY() - secondPoint.getY())) * 180 / Math.PI);

	    }//End if((secondPoint.x > firstPoint.x) && (secondPoint.y <= firstPoint.y))

	    return Math.atan2(0 ,0);

	}
	
	public static double getAngleFromPoint(float x1, float y1, float x2, float y2) {

	    if(x2 > x1) {//above 0 to 180 degrees

	        return (Math.atan2(x2 - x1, y1 - y2) * 180 / Math.PI);

	    }
	    else if(x2 < x1) {//above 180 degrees to 360/0

	        return 360 - (Math.atan2(x1 - x2, y1 - y2) * 180 / Math.PI);

	    }//End if((secondPoint.x > firstPoint.x) && (secondPoint.y <= firstPoint.y))

	    return Math.atan2(0 ,0);

	}
	
}
