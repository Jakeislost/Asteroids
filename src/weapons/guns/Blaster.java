package weapons.guns;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import handlers.Handler;
import objects.Player;
import weapons.objects.Bullet;
import weapons.objects.Weapon;

public class Blaster extends Weapon{
	
	public Blaster(Handler handler) {
		super(handler);
		setName("Blaster");
		setLife(30);
		setROF(8);
		setLevelCap(4);
		try {
			fireSound = new Sound("res/sounds/fire.wav");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fire(Player player, int delta) {
		if(timer.isDone()) {
			
			if(player.firing) {
				timer.resetTimer();
				if(fireSound != null)fireSound.play();
				if(level == 0 || level == 2 || level == levelCap) {
					handler.addOject(new Bullet(player.getPolygon().getCenterX(), player.getPolygon().getCenterY(), player.angle, life, true));
				}
				if(level != 0) {
					handler.addOject(new Bullet(player.getPolygon().getCenterX(), player.getPolygon().getCenterY(), player.angle - 0.04f, life, true));
					handler.addOject(new Bullet(player.getPolygon().getCenterX(), player.getPolygon().getCenterY(), player.angle + 0.04f, life, true));
				}
				if(level == 3 || level == levelCap) {
					handler.addOject(new Bullet(player.getPolygon().getCenterX(), player.getPolygon().getCenterY(), player.angle - 0.08f, life, true));
					handler.addOject(new Bullet(player.getPolygon().getCenterX(), player.getPolygon().getCenterY(), player.angle + 0.08f, life, true));
				}
			}
			
		}
		timer.update();
		
	}

	@Override
	public void draw(Graphics g) {
		
	}

}
