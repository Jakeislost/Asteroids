package objects;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import resources.Util;
import states.Game;

public class Player extends GameObject{
	
	//define each level
	private enum levels {
		lvl1(10, 1),
		lvl2(10, 2),
		lvl3(8, 3),
		lvl4(6, 4),
		lvl5(6, 5);
		
		int ROF, id;
		
		levels(int ROF, int id) {
			this.ROF = ROF;
			this.id = id;
		}
		
		public int getROF() {
			return ROF;
		}
		public int getID() {
			return id;
		}
		
		
	}

	//movement variables
	private float moveSpeed = 0.2f, maxMoveSpeed = 3.3f, deceleration = 0.003f;
	private float angle, rotateSpeed = 0.1f;
	private float xDir, yDir; // direction values (can be positive/negative
	private Polygon ship;
	
	//firing variables
	private boolean firing = false;
	private int delay;
	private levels level = levels.lvl1;
	
	//pulse ability
	private int pulseLimit = 1000, pulseCharge = pulseLimit;
	private int pulseLife, pulseLifeLimit = 50;
	private Circle pulse = null;
	private boolean pulseFired;
	
	//HUD elements
	private int livesAmount = 3, livesLimit = 5, extraLives = 1, barOffset = 10;
	
	//invincibility after getting hit
	private boolean inv = false;
	private int invFrames, limit = 200;
	
	/*
	 * The particle system does not support on off state
	 * whilst particles are active
	 * (they get deleted when not enabled)
	 * so we set the life value of the particles
	 * in order to stop/start production of new particles
	 * from showing
	 */
	private ParticleSystem pSys;
	private ConfigurableEmitter emitter;
	private Point pLife = new Point(1000, 800); // point to hold min/max values (easy to change from variable)
	
	//sounds
	private Sound shoot, thrust, life;
	
	public Player(float x, float y, float startAngle) {
		super(x, y);
		
		//construct ship
		ship = new Polygon();
		
		ship.addPoint(9, 0);
		ship.addPoint(17, 21);
		ship.addPoint(9, 15);
		ship.addPoint(0, 21);
		
		ship.setCenterX(x);
		ship.setCenterY(y);
		
		angle = startAngle;
		ship = (Polygon) ship.transform(Transform.createRotateTransform(angle, ship.getCenterX(), ship.getCenterY()));
		
		//load particles
		try {
			Image particle = Util.getImage("particle");
			pSys = new ParticleSystem(particle, 700);
			
			emitter = Util.getEmitter("rocket").duplicate();
			emitter.setPosition(x, y, false);
			pSys.addEmitter(emitter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		pSys.setBlendingMode(ParticleSystem.BLEND_COMBINE);
		emitter.initialLife.setMax(1);
		emitter.initialLife.setMin(0);
		
		try {
			life = new Sound("res/sounds/extraShip.wav");
			shoot = new Sound("res/sounds/fire.wav");
			thrust = new Sound("res/sounds/thrust.wav");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta, ArrayList<GameObject> object) {
		//movement
		this.ship = (Polygon)this.ship.transform(Transform.createTranslateTransform(yDir, xDir * -1));
        updateShip(); // enable ship to loop edges
        //set emitter rotation to back of ship
		emitter.angularOffset.setValue((float) (Math.toDegrees(angle) - 180));
		emitter.setPosition(ship.getCenterX(), ship.getCenterY(), false);
        pSys.update(delta);
        
        //every 20,000 points, gain 1 life
        if(Game.score >= 20000 * extraLives) { 
        	life.play();
        	if(livesAmount < livesLimit)livesAmount++;
        	extraLives++;
        }
        
        //firing mechanism
        if(delay < level.getROF())delay++;
        if(firing) { 
        	if(delay >= level.getROF()) {
        		fire(object);
        		if(shoot != null)shoot.play();
        		delay = 0;
        	}
        }
        if(pulseCharge < pulseLimit) pulseCharge++;
        if(pulseFired) {
        	if(pulseCharge >= pulseLimit) {
        		pulseCharge = 0;
        		pulse = new Circle(ship.getCenterX(), ship.getCenterY(), 5);
        	}
        	pulseFired = false;
        }
    	if(pulse != null) {
    		pulseLife++;
    		if(pulseLife < pulseLifeLimit) {
    			pulse.setRadius(pulse.getRadius() + 5);
    			pulse.setCenterX(ship.getCenterX());
    			pulse.setCenterY(ship.getCenterY());
    		}
    		for(GameObject o : new ArrayList<GameObject>(object)) {
    			if(o instanceof Asteroid) {
    				if(pulse.contains(((Asteroid) o).getPolygon().getCenterX(), ((Asteroid) o).getPolygon().getCenterY())) {
    					((Asteroid) o).destroy(object, true);
    				}
    			} else if(o instanceof UFO) {
    				if(pulse.contains(((UFO) o).getPolygon().getCenterX(), ((UFO) o).getPolygon().getCenterY())) {
    					((UFO) o).destroy();
    				}
    			}
    		}
    		if(pulseLife >= pulseLifeLimit) { 
    			pulse = null;
    			pulseLife = 0;
    		}
    	}
        
        //collision
        if(!inv) {
	       	for(GameObject o : object) {
	        	if(o instanceof Asteroid) {
	        		if(((Asteroid) o).getPolygon().contains(ship.getCenterX(), ship.getCenterY()) && !((Asteroid) o).getHit()) {
	        			livesAmount--;
	        			if(livesAmount == 0) {
	        				gsm.enterState(2);
	        				((Game)gsm.getCurrentState()).reset(gc, gsm);
	        			}
	        			invFrames = 0;
	        			inv = true;
	        			level = levels.lvl1;
	        			((Asteroid) o).destroy(object, false);
	        			break;
	        		}
	        	} else if(o instanceof Bullet) {
	        		if(!((Bullet) o).getType() && new Rectangle(ship.getCenterX() - ship.getWidth(), 
	        				ship.getCenterY() - ship.getWidth(),
	        				ship.getWidth(), 
	        				ship.getWidth()).contains(
	        						((Bullet) o).getPolygon().getCenterX(),
	        						((Bullet) o).getPolygon().getCenterY())) {
	        			livesAmount--;
	        			if(livesAmount == 0) {
	        				gsm.enterState(2);
	        				((Game)gsm.getCurrentState()).reset(gc, gsm);
	        			}
	        			invFrames = 0;
	        			inv = true;
	        			level = levels.lvl1;
	        			object.remove(o);
	        			break;
	        		}
	        	}
	        }
        } else {
        	//if currently invincible, increase the timer
        	invFrames++;
        	if(invFrames > limit) {
        		inv = false;
        		invFrames = 0;
        	}
        }
        
        //collision for powerup (laziness so theres an extra for loop)
        for(GameObject o : object) {
        	if(o instanceof Powerup) {
        		if(((Powerup) o).getPolygon().contains(ship.getCenterX(), ship.getCenterY())) {
        			object.remove(o);
        			switch(level) {
        				case lvl1 :
        					level = levels.lvl2;
        					break;
        				case lvl2 : 
        					level = levels.lvl3;
        					break;
        				case lvl3 : 
        					level = levels.lvl4;
        					break;
        				case lvl4 : 
        					level = levels.lvl5;
        					break;
        				case lvl5 : 
        					Game.score += 20000;
        					break;
        			}
        			break;
        		}
        	}
        }
        
	}

	@Override
	public void draw(Graphics g) {
		//clip for cleaner graphics
		g.setColor((!inv) ? Color.white : Color.red);
		g.setClip(Game.GAME_START_X, Game.GAME_START_Y, Game.GAME_END_X - Game.GAME_START_X, Game.GAME_END_Y - Game.GAME_START_Y);
		pSys.render();
		g.fill(ship);
		g.setColor(Color.yellow);
		if(pulse != null) g.draw(pulse);
		g.clearClip();
		
		//HUD
		g.setColor(new Color(120, 250, 120));
		g.setColor(Color.red);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y + barOffset, livesAmount * 300 / livesLimit, 10);
		g.setColor(Color.cyan);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 12, delay * 300 / level.getROF(), 10);
		g.setColor(Color.green);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 24, level.getID() * 300 / 5, 10);
		g.setColor(Color.magenta);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 36, pulseCharge * 300 / pulseLimit, 10);
		g.setColor(Color.white);
		g.drawRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset, 300, 10);
		g.drawRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 12, 300, 10);
		g.drawRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 24, 300, 10);
		g.drawRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 36, 300, 10);
		
	}

	@Override
	public void input(Input IM) {
		//rotation
		if(IM.isKeyDown(IM.KEY_A)) {
			ship = (Polygon) ship.transform(Transform.createRotateTransform(-rotateSpeed, ship.getCenterX(), ship.getCenterY()));
			angle -= rotateSpeed;
			if(angle < 0) angle += 2*Math.PI;
		} else if(IM.isKeyDown(IM.KEY_D)) {
			ship = (Polygon) ship.transform(Transform.createRotateTransform(rotateSpeed, ship.getCenterX(), ship.getCenterY()));
			angle += rotateSpeed;
			if(angle > 2*Math.PI) angle -= 2*Math.PI;
		}
		//acceleration / deceleration
		if(IM.isKeyDown(IM.KEY_W)) {
//			currentSpeed += moveSpeed;
//			if(currentSpeed > maxMoveSpeed) currentSpeed = maxMoveSpeed;
			xDir += (float) (moveSpeed*Math.cos(angle));
			yDir += (float) (moveSpeed*Math.sin(angle));
			if(xDir > maxMoveSpeed) xDir = maxMoveSpeed;
			else if(xDir < -maxMoveSpeed) xDir = -maxMoveSpeed;
			if(yDir > maxMoveSpeed) yDir = maxMoveSpeed;
			else if(yDir < -maxMoveSpeed) yDir = -maxMoveSpeed;
			
			if(thrust != null && !thrust.playing())thrust.play();
			
			emitter.initialLife.setMax(pLife.getX());
			emitter.initialLife.setMin(pLife.getY());
		} else {
			if(thrust != null)thrust.stop();
			emitter.initialLife.setMax(1);
			emitter.initialLife.setMin(0);
			xDir = (xDir < 0) ? xDir + deceleration : xDir - deceleration;
			yDir = (yDir < 0) ? yDir + deceleration : yDir - deceleration;
			
		}
		if(IM.isKeyDown(IM.KEY_SPACE) || IM.isMouseButtonDown(0)) firing = true;
		else firing = false;
		
		if(IM.isKeyDown(IM.KEY_E) || IM.isMouseButtonDown(IM.MOUSE_RIGHT_BUTTON)) pulseFired = true;
		else pulseFired = false;
		
		/*
		 * this allowed me to better configure the look
		 * and style of the thrusters
		 * it is just a method of resetting the emitter
		 */
		if(IM.isKeyDown(IM.KEY_N)) {
			pSys.removeAllEmitters();
			try {
				emitter = ParticleIO.loadEmitter(ResourceLoader.getResourceAsStream("res/xml/Rocket.xml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			emitter.setPosition(ship.getCenterX(), ship.getCenterY(), false);
			pSys.addEmitter(emitter);
		}
	}
	
	private void fire(ArrayList<GameObject> object) {
		
		switch(level) {
		case lvl1 :
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), angle, true));
			break;
		case lvl2 : 
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle - 0.1), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle + 0.1), true));
			break;
		case lvl3 : 
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle - 0.1), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), angle, true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle + 0.1), true));
			break;
		case lvl4 : 
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle - 0.2), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle - 0.1), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle + 0.1), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle + 0.2), true));
			break;
		case lvl5 : 
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle - 0.2), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle - 0.1), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), angle, true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle + 0.1), true));
			object.add(new Bullet(ship.getCenterX(), ship.getCenterY(), (float) (angle + 0.2), true));
			break;
		}
	}
	
	private void updateShip()
    {
		if(ship.getCenterX() > Game.GAME_END_X){
            this.ship.setCenterX(Game.GAME_START_X);
        }
        else if(ship.getCenterX() < Game.GAME_START_X)
        {
            this.ship.setCenterX(Game.GAME_END_X);
        }
        if(ship.getCenterY() < Game.GAME_START_Y)
        {
            this.ship.setCenterY(Game.GAME_END_Y);
        }
        else if(ship.getCenterY() > Game.GAME_END_Y)
        {
            this.ship.setCenterY(Game.GAME_START_Y);
        }         
    }

	public Polygon getPolygon() {
		return ship;
	}
	
}
