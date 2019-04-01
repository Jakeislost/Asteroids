package objects;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import resources.Util;
import states.Game;

public class UFO extends GameObject{
	
	//movement, shape;
	private float speed = 1;
	private float angle;
	private Polygon UFO;
	private Line[] UFOLine;
	
	private boolean hit;
	private int life, lifeTime = 10;
	private ParticleSystem pSys;
	private ConfigurableEmitter emitter;
	
	private Sound siren;
	
	private int delay, wait = 60, shoot = 15;

	public UFO() {
		super(Game.GAME_END_X + 50, 0);
		Random rand = new Random();
		y = (rand.nextInt(2) == 1) ? Game.GAME_START_Y + 50 : Game.GAME_END_Y - 50;
		
		createUFO();
		
		//setup explosion particles
		try {
			Image particle = Util.getImage("particle");
			pSys = new ParticleSystem(particle, 500);
			
			emitter = Util.getEmitter("explosionMedium").duplicate();
			emitter.setPosition(UFO.getCenterX(), UFO.getCenterY(), false);
			pSys.addEmitter(emitter);
			
			siren = new Sound("res/sounds/saucerBig.wav");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pSys.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta, ArrayList<GameObject> object) {
		if(!hit) {
			moveUFO(-speed);
			
			for(GameObject o : object) {
				if(o instanceof Bullet) {
					if(UFO.contains(((Bullet) o).getPolygon().getCenterX(), ((Bullet) o).getPolygon().getCenterY()) && ((Bullet)o).getType()) {
						destroy();
						object.remove(o);
						break;
					}
				}
			}
			
			delay++;
			if(delay == wait) {
				for(GameObject o : object) {
					if(o instanceof Player) {
						angle = 0;
						angle = (float) Math.toRadians(getAngleFromPoint(
								new Point(UFO.getCenterX(), UFO.getCenterY()), 
								new Point(((Player) o).getPolygon().getCenterX(), ((Player) o).getPolygon().getCenterY())
								));
						
						break;
					}
				}
			} else if(delay >= wait + shoot) {
				delay = 0;
				object.add(new Bullet(UFO.getCenterX(), UFO.getCenterY(), angle, false));
			}
			if(!siren.playing()) siren.loop();
		} else {
			siren.stop();
			//play explosion and increment lifetime until death
	       	life++;
			emitter.setPosition(UFO.getCenterX(), UFO.getCenterY(), false);
			pSys.update(delta);
			if(life > lifeTime) {
				emitter.wrapUp();
				if(pSys.getParticleCount() == 0) object.remove(this);
			}
		}
		
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.red);
		g.setClip(Game.GAME_START_X, Game.GAME_START_Y, Game.GAME_END_X - Game.GAME_START_X, Game.GAME_END_Y - Game.GAME_START_Y);
		if(!hit) {
			g.draw(UFO);
			for(Line l : UFOLine) {
				g.draw(l);
			}
		} else pSys.render();
		
		g.clearClip();
	}
	
	public double getAngleFromPoint(Point firstPoint, Point secondPoint) {

	    if((secondPoint.getX() > firstPoint.getX())) {//above 0 to 180 degrees

	        return (Math.atan2((secondPoint.getX() - firstPoint.getX()), (firstPoint.getY() - secondPoint.getY())) * 180 / Math.PI);

	    }
	    else if((secondPoint.getX() < firstPoint.getX())) {//above 180 degrees to 360/0

	        return 360 - (Math.atan2((firstPoint.getX() - secondPoint.getX()), (firstPoint.getY() - secondPoint.getY())) * 180 / Math.PI);

	    }//End if((secondPoint.x > firstPoint.x) && (secondPoint.y <= firstPoint.y))

	    return Math.atan2(0 ,0);

	}
	
	public void moveUFO(float xDir) {
		UFO = (Polygon) UFO.transform(Transform.createTranslateTransform(xDir, 0));
		
		if(UFO.getCenterX() < Game.GAME_START_X + UFO.getWidth() / 2) UFO.setCenterX(Game.GAME_END_X + UFO.getWidth());
		
		UFOLine[0].setCenterX(UFO.getCenterX());
		UFOLine[0].setCenterY(UFO.getCenterY() - 1);
		UFOLine[1].setCenterX(UFO.getCenterX());
		UFOLine[1].setCenterY(UFO.getCenterY() + 4);
	}
	
	public void createUFO() {
		UFO = new Polygon();
		UFOLine = new Line[2];
		
		UFO.addPoint(10, 10);
		UFO.addPoint(14, 0);
		UFO.addPoint(36, 0);
		UFO.addPoint(40, 10);
		UFO.addPoint(50, 15);
		UFO.addPoint(40, 20);
		UFO.addPoint(10, 20);
		UFO.addPoint(0, 15);
		
		UFO.setCenterX(x);
		UFO.setCenterY(y);
		
		UFOLine[0] = new Line(10, 10, 40, 10);
		UFOLine[0].setCenterX(UFO.getCenterX());
		UFOLine[0].setCenterY(UFO.getCenterY() - 1);
		UFOLine[1] = new Line(0, 15, 50, 15);
		UFOLine[1].setCenterX(UFO.getCenterX());
		UFOLine[1].setCenterY(UFO.getCenterY() + 4);
		
	}

	@Override
	public void input(Input IM) {
		if(IM.isKeyPressed(IM.KEY_N)) createUFO();
	}
	
	public void destroy() {
		if(!hit) {
			Game.score += 500;
			hit = true;
		}
	}
	
	public Polygon getPolygon() {
		return UFO;
	}

}
