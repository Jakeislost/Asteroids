package handlers;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import objects.GameObject;

public class Handler {

	private ArrayList<GameObject> object = new ArrayList<GameObject>();
	
	public ArrayList<GameObject> getObjectList() {
		return object;
	}
	public void addOject(GameObject o) {
		this.object.add(o);
	}
	public void removeObject(GameObject o) {
		this.object.remove(o);
	}
	public void clearList() {
		this.object.clear();
	}
	public void draw(Graphics g) {
		for(GameObject o : new ArrayList<GameObject>(object)) o.draw(g);
	}
	public void update(GameContainer gc, StateBasedGame gsm, int delta) {
		for(GameObject o : new ArrayList<GameObject>(object)) o.update(gc, gsm, delta, object);
	}
	public void input(Input IM) { 
		for(GameObject o : new ArrayList<GameObject>(object)) o.input(IM);
	}
	
	
}
