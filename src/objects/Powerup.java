package objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import resources.Util;
import states.Game;

public class Powerup extends GameObject{

	//shape, movement and direction
	private Polygon box;
	private float speed = 0.5f, angle;
	
	//particle variables
	private boolean pickedUp = false; // this ensures particles have enough time to end before deletion
	private ParticleSystem pSys;
	private ConfigurableEmitter emitter;
	
	public Powerup(float x, float y) {
		super(x, y);
		
		//create box (so it can rotate)
		createBox();
		
		//load particles
		try {
			Image particle = Util.getImage("particle");
			pSys = new ParticleSystem(particle, 500);
			
			emitter = Util.getEmitter("powerup").duplicate();
			emitter.setPosition(box.getCenterX(), box.getCenterY(), false);
			pSys.addEmitter(emitter);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//random direction
		Random rand = new Random();
		angle = (float) Math.toRadians(rand.nextInt(360));
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta, ArrayList<GameObject> object) {
		if(!pickedUp) {
			// if not picked up by the player, keep moving
			box = (Polygon) box.transform(Transform.createRotateTransform(-0.05f, box.getCenterX(), box.getCenterY()));
			this.box = (Polygon)this.box.transform(Transform.createTranslateTransform((float)(speed*Math.sin(angle)),(float)(speed*Math.cos(angle)*-1)));
			emitter.setPosition(box.getCenterX(), box.getCenterY(), false);
			updateBox(); // loop box around edges
		} else {
			//if picked up, wrap up particles before deletion
			emitter.wrapUp();
			if(pSys.getParticleCount() == 0) object.remove(this);
		}
		pSys.update(delta);
	}

	@Override
	public void draw(Graphics g) {
		//clip for cleaner graphics
		g.setClip(Game.GAME_START_X, Game.GAME_START_Y, Game.GAME_END_X - Game.GAME_START_X, Game.GAME_END_Y - Game.GAME_START_Y);
		pSys.render();
		if(!pickedUp) {
			g.setColor(Color.yellow);
			g.fill(box);
		}
		g.clearClip();
		
	}
	
	public void destroy() {
		//function to ensure particles disappear before deletion
		pickedUp = true;
	}
	
	public void createBox() {
		box = new Polygon();
		box.addPoint(20, 0);
		box.addPoint(40, 20);
		box.addPoint(20, 40);
		box.addPoint(0, 20);
		
		box.setCenterX(x);
		box.setCenterY(y);
		
	}
	
	private void updateBox()
    {
		if(box.getCenterX() > Game.GAME_END_X + box.getWidth()){
            this.box.setCenterX(Game.GAME_START_X - box.getWidth());
        }
        else if(box.getCenterX() < Game.GAME_START_X - box.getWidth())
        {
            this.box.setCenterX(Game.GAME_END_X + box.getWidth());
        }
        if(box.getCenterY() < Game.GAME_START_Y - box.getHeight())
        {
            this.box.setCenterY(Game.GAME_END_Y + box.getHeight());
        }
        else if(box.getCenterY() > Game.GAME_END_Y + box.getHeight())
        {
            this.box.setCenterY(Game.GAME_START_Y - box.getHeight());
        }         
    }

	@Override
	public void input(Input IM) {
		if(IM.isKeyDown(IM.KEY_N)) {
			pSys.removeAllEmitters();
			try {
				emitter = ParticleIO.loadEmitter(ResourceLoader.getResourceAsStream("res/xml/powerup.xml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			emitter.setPosition(box.getCenterX(), box.getCenterY(), false);
			pSys.addEmitter(emitter);
		}
	}

	public Polygon getPolygon() {
		return box;
	}
	
	public boolean isPickedup() {
		return pickedUp;
	}
	
}
