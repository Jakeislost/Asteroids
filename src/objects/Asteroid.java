package objects;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import resources.AsteroidHandler;
import resources.Util;
import states.Game;

public class Asteroid extends GameObject{

	//define shape of the asteroid
	public enum AsteroidType {
		large(),
		medium(),
		small();
	}
	
	//shape, movement and type
	private AsteroidType type;
	public float angle, speed;
	private Polygon Asteroid;
	
	//explosion particles + explosion life
	private ParticleSystem pSys;
	private ConfigurableEmitter emitter;
	private int life, limit = 10;
	private boolean hit;
	
	private Sound explosion;
	
	public Asteroid(float x, float y, AsteroidType type, float angle) {
		super(x, y);
		this.type = type;
		this.angle = angle;
		Asteroid = AsteroidHandler.createAsteroid(this);
		
		//setup explosion particles
		try {
			Image particle = Util.getImage("particle");
			pSys = new ParticleSystem(particle, 500);
			switch(type) {
			case large :
				emitter = Util.getEmitter("explosionLarge").duplicate();
				break;
			case medium :
				emitter = Util.getEmitter("explosionMedium").duplicate();
				break;
			case small :
				emitter = Util.getEmitter("explosionSmall").duplicate();
				break;
			}
			emitter.setPosition(Asteroid.getCenterX(), Asteroid.getCenterY(), false);
			pSys.addEmitter(emitter);
			
			switch(type) {
				case large :
					explosion = new Sound("res/sounds/bangLarge.wav");
					break;
				case medium :
					explosion = new Sound("res/sounds/bangMedium.wav");
					break;
				case small :
					explosion = new Sound("res/sounds/bangSmall.wav");
					break;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pSys.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		
		
		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta, ArrayList<GameObject> object) {
        if(hit) {
        	//play explosion and increment lifetime until death
	       	life++;
			emitter.setPosition(Asteroid.getCenterX(), Asteroid.getCenterY(), false);
			pSys.update(delta);
			if(life > limit) {
				emitter.wrapUp();
				if(pSys.getParticleCount() == 0) object.remove(this);
			}
        } else {
        	//movement
	        Asteroid = (Polygon) Asteroid.transform(Transform.createRotateTransform(-0.05f, Asteroid.getCenterX(), Asteroid.getCenterY()));
			this.Asteroid = (Polygon)this.Asteroid.transform(Transform.createTranslateTransform((float)(speed*Math.sin(angle)),(float)(speed*Math.cos(angle)*-1)));
	        updateAsteroid(); // loop round edges
        }
		
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.white);
		//clip for cleaner graphics
		g.setClip(Game.GAME_START_X, Game.GAME_START_Y, Game.GAME_END_X - Game.GAME_START_X, Game.GAME_END_Y - Game.GAME_START_Y);
		if(!hit)g.draw(Asteroid);
		else pSys.render();
		g.clearClip();
	}
	
	/*
	 * this loop code is different for the asteroid
	 * as it can be the biggest object in the playable area
	 * therefore allowing it to slightly off screen
	 * before teleporting to the other side
	 * lets it look better as well as avoid
	 * any sudden appearances of an asteroid ontop
	 * of the player
	 */
	private void updateAsteroid()
    {
		if(Asteroid.getCenterX() > Game.GAME_END_X + Asteroid.getWidth() / 2){
            this.Asteroid.setCenterX(Game.GAME_START_X - Asteroid.getWidth() / 2);
        }
        else if(Asteroid.getCenterX() < Game.GAME_START_X - Asteroid.getWidth() / 2)
        {
            this.Asteroid.setCenterX(Game.GAME_END_X + Asteroid.getWidth() / 2);
        }
        if(Asteroid.getCenterY() < Game.GAME_START_Y - Asteroid.getHeight() / 2)
        {
            this.Asteroid.setCenterY(Game.GAME_END_Y + Asteroid.getHeight() / 2);
        }
        else if(Asteroid.getCenterY() > Game.GAME_END_Y + Asteroid.getHeight() / 2)
        {
            this.Asteroid.setCenterY(Game.GAME_START_Y - Asteroid.getHeight() / 2);
        }         
    }
	
	//this function is called by the bullet when collision occurs
	public void destroy(ArrayList<GameObject> object, boolean pulse) {
		if(!hit) {
			Random rand = new Random();
			/*
			 * depending on the asteroids type, there will be
			 * a different amount of score to give and asteroids to spawn.
			 * therefore there is a switch statement to define what to do
			 * when hit
			 */
			if(!pulse) {
				switch(type) {
					case large :
						object.add(new Asteroid(getPolygon().getCenterX(), getPolygon().getCenterY(), AsteroidType.medium, (float) Math.toRadians(rand.nextInt(360))));
						object.add(new Asteroid(getPolygon().getCenterX(), getPolygon().getCenterY(), AsteroidType.medium, (float) Math.toRadians(rand.nextInt(360))));
						object.add(new Asteroid(getPolygon().getCenterX(), getPolygon().getCenterY(), AsteroidType.medium, (float) Math.toRadians(rand.nextInt(360))));
						Game.score += 100;
						break;
					case medium :
						object.add(new Asteroid(getPolygon().getCenterX(), getPolygon().getCenterY(), AsteroidType.small, (float) Math.toRadians(rand.nextInt(360))));
						object.add(new Asteroid(getPolygon().getCenterX(), getPolygon().getCenterY(), AsteroidType.small, (float) Math.toRadians(rand.nextInt(360))));
						Game.score += 130;
						break;
					case small :
						//small asteroids dont spawn any asteroids but give points
						Game.score += 225;
						break;
				}
			} else Game.score += 200;
			//random chance to spawn powerup for player
			if(rand.nextInt(100) == 1)object.add(new Powerup(getPolygon().getCenterX(), getPolygon().getCenterY()));
			hit = true;
			explosion.play();
		}
		
	}
	
	public Polygon getPolygon() {
		return Asteroid;
	}
	public AsteroidType getType() {
		return type;
	}
	
	public boolean getHit() {
		return hit;
	}

	@Override
	public void input(Input IM) {
		if(IM.isKeyDown(IM.KEY_N)) Asteroid = AsteroidHandler.createAsteroid(this);
	}

}
