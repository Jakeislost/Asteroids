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
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;

import resources.FrameTimer;
import resources.Util;
import states.Game;
import weapons.objects.Bullet;

public class UFO extends GameObject{
	
	//movement, shape;
	private float speed = 1;
	private float angle;
	private int iterations = 20;
	private Polygon UFO;
	private Line[] UFOLine;
	
	private boolean hit;
	private ParticleSystem pSys;
	private ConfigurableEmitter emitter;
	
	private Sound siren;
	
	private FrameTimer timer;
	

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
		timer = new FrameTimer(60, false);
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
			
			timer.update();
			if(timer.isDone()) {
				timer.resetTimer();
				for(GameObject o : object) {
					if(o instanceof Player) {
						
						Polygon ship = ((Player) o).getPolygon().copy();
						float xDir = ((Player) o).xDir;
						float yDir = ((Player) o).yDir;
						for(int i = 0; i < iterations; i++) {
							ship = (Polygon) ship.transform(Transform.createTranslateTransform(yDir, xDir * -1));
						}
						
						angle = 0;
						angle = (float) Math.toRadians(Util.getAngleFromPoint(
								new Point(UFO.getCenterX(), UFO.getCenterY()), 
								new Point(ship.getCenterX(), ship.getCenterY())
								));
						
						object.add(new Bullet(UFO.getCenterX(), UFO.getCenterY(), angle, 100, false));
						
						break;
					}
				}
			}
			if(!siren.playing()) siren.loop();
		} else {
			siren.stop();
			//play explosion and increment lifetime until death
			emitter.setPosition(UFO.getCenterX(), UFO.getCenterY(), false);
			pSys.update(delta);
			if(pSys.getParticleCount() == 0) object.remove(this);
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
	
	public void dispose() {
		if(siren.playing()) siren.stop();
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
