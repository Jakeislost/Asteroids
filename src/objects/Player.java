package objects;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;

import resources.FrameTimer;
import resources.Util;
import states.Game;
import weapons.objects.Bullet;
import weapons.objects.Weapon;

public class Player extends GameObject{

	//movement variables
	private float moveSpeed = 0.1f, maxMoveSpeed = 3.3f, deceleration = 0.003f;
	public float angle;
	private float rotateSpeed = 0.1f;
	public float xDir, yDir; // direction values (can be positive/negative
	private Polygon ship;
	
	//firing variables
	public boolean firing;
	private Weapon weap;
	
	//pulse ability
	private Weapon ability;
	public boolean abilityFired;
	
	//HUD elements
	private int livesAmount = 3, livesLimit = 5, extraLives = 1, barOffset = 10;
	
	//invincibility after getting hit
	private FrameTimer invTimer;
	
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
	private Sound thrust, life;
	
	public Player(float x, float y, float startAngle, Weapon weap, Weapon ability) {
		super(x, y);
		this.weap = weap;
		this.ability = ability;
		
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
			thrust = new Sound("res/sounds/thrust.wav");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		invTimer = new FrameTimer(200, false);
		invTimer.pause();
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
        weap.fire(this, delta);
        
        //pulse ability
        ability.fire(this, delta);
        
        //collision
        for(GameObject o : new ArrayList<GameObject>(object)) {
        	
        	if(invTimer.isPaused()) {
	        	if(o instanceof Asteroid) {
	        		//explode asteroid on impact and damage player
	        		if(!((Asteroid) o).getHit()) {
	        			if(((Asteroid) o).getPolygon().contains(ship.getCenterX(), ship.getCenterY())) {
	        				((Asteroid) o).destroy(object, false);
	        				damagePlayer(gc, gsm);
	        			}
	        		}
	        		
	        	} else if(o instanceof Bullet) {
	        		// damage player on impact
	        		if(!((Bullet) o).getType()) {
		        		if(ship.contains(((Bullet) o).getPolygon().getCenterX(), ((Bullet) o).getPolygon().getCenterY())) {
		        			object.remove(o);	
		        			damagePlayer(gc, gsm);
		        		}
	        		}
	        		
	        		
	        	}
        	}
        	if(o instanceof Powerup) {
        		//collect powerup and increment weapon level
        		if(!((Powerup) o).isPickedup()) {
	        		if(((Powerup) o).getPolygon().contains(ship.getCenterX(), ship.getCenterY())) {
	        			((Powerup) o).destroy();
	        			weap.incrementLevel();
	        			Game.score += 500;
	        			ability.completeTimer();
	        		}
        		}
        		
        		
        	}
        	
        }
        
        //invincibility Timer update
        invTimer.update();
        if(invTimer.isDone()) {
        	invTimer.resetTimer();
        	invTimer.pause();
        }
	}

	@Override
	public void draw(Graphics g) {
		//clip for cleaner graphics
		g.setColor((invTimer.isPaused()) ? Color.white : Color.red);
		g.setClip(Game.GAME_START_X, Game.GAME_START_Y, Game.GAME_END_X - Game.GAME_START_X, Game.GAME_END_Y - Game.GAME_START_Y);
		pSys.render();
		g.fill(ship);
		ability.draw(g);
		g.clearClip();
		
		//HUD
		g.setColor(new Color(120, 250, 120));
		g.setColor(Color.red);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y + barOffset, livesAmount * 300 / livesLimit, 10);
		g.setColor(Color.cyan);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 12, weap.getDelay() * 300 / weap.getROF(), 10);
		g.setColor(Color.green);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 24, weap.getLevel() * 300 / weap.getLevelCap(), 10);
		g.setColor(Color.magenta);
		g.fillRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + 36, ability.getDelay() * 300 / ability.getROF(), 10);
		g.setColor(Color.white);
		for(int i = 0 ; i < 4; i++) {
			g.drawRect(Game.GAME_START_X, Game.GAME_END_Y  + barOffset + i * 12, 300, 10);
			
		}
		
	}
	
	public void damagePlayer(GameContainer gc, StateBasedGame gsm) {
		livesAmount--;
		if(livesAmount <= 0) {
			gsm.enterState(2);
			((Game)gsm.getState(1)).reset(gc, gsm);
		}
		invTimer.start();
		weap.resetLevel();
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
		
		if(IM.isKeyDown(IM.KEY_E) || IM.isMouseButtonDown(IM.MOUSE_RIGHT_BUTTON)) abilityFired = true;
		else abilityFired = false;
		
		/*
		 * this allowed me to better configure the look
		 * and style of the thrusters
		 * it is just a method of resetting the emitter
		 */
		if(IM.isKeyDown(IM.KEY_N)) {
			ship = (Polygon) ship.transform(Transform.createTranslateTransform(0, 5));
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
	public void setPolygon(Polygon ship) {
		this.ship = ship;
	}
	
}
