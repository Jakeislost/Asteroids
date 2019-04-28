package weapons.abilities;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.particles.ParticleSystem;

import handlers.Handler;
import objects.Asteroid;
import objects.GameObject;
import objects.Player;
import objects.UFO;
import resources.Util;
import states.Game;
import weapons.objects.Weapon;

public class Pulse extends Weapon{

	private int lifeTimer;
	private Circle pulse = null;
	
	private ParticleSystem pSys;
	
	public Pulse(Handler handler) {
		super(handler);
		setName("Pulse");
		setROF(1000);
		setLife(60);
		
		pSys = new ParticleSystem(Util.getImage("particle"), 1000);
		
	}

	@Override
	public void fire(Player player, int delta) {
		timer.update();
		if(timer.isDone()) {
			if(player.abilityFired) {
				timer.resetTimer();
				pulse = new Circle(player.getPolygon().getCenterX(), player.getPolygon().getCenterY(), 5);
				pSys.addEmitter(Util.getEmitter("pulse").duplicate());
			}
		}
		if(pulse != null) {
			isActive = true;
			if(lifeTimer < life) { 
				lifeTimer++;
				pulse.setRadius(pulse.getRadius() + 6);
				pulse.setCenterX(player.getPolygon().getCenterX());
				pulse.setCenterY(player.getPolygon().getCenterY());
				pSys.setPosition(player.getPolygon().getCenterX(), player.getPolygon().getCenterY());
				collision();
				
			} else { 
				lifeTimer = 0;
				pulse = null;
				isActive = false;
			}
			
		}
		if(pSys.getEmitterCount() > 0) { 
			pSys.update(delta);
		}
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.yellow);
		//if(pulse != null) g.draw(pulse);
		if(pSys.getParticleCount() > 0)pSys.render();
	}
	
	public void collision() {
		for(GameObject o : new ArrayList<GameObject>(handler.getObjectList())) {
			if(o instanceof Asteroid) {
				if(!((Asteroid) o).getHit() && pulse.contains(((Asteroid) o).getPolygon().getCenterX(), ((Asteroid) o).getPolygon().getCenterY())) {
					((Asteroid) o).destroy(handler.getObjectList(), true);
					Game.score += 200;
				}
			} else if(o instanceof UFO) {
				if(pulse.contains(((UFO) o).getPolygon().getCenterX(), ((UFO) o).getPolygon().getCenterY())) {
					((UFO) o).destroy();
				}
			}
		}
	}

}
