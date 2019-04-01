package resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
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
	
}
