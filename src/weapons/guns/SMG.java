package weapons.guns;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import handlers.Handler;
import objects.Player;
import weapons.objects.Bullet;
import weapons.objects.Weapon;

public class SMG extends Weapon{

	public SMG(Handler handler) {
		super(handler);
		setName("SMG");
		setROF(4);
		setLife(20);
		setLevelCap(2);
	}

	@Override
	public void fire(Player player, int delta) {
		if(timer.isDone()) {
			
			if(player.firing) {
				timer.resetTimer();
				Random rand = new Random();
				float chance = (rand.nextFloat() * 2) - 1f;
				handler.addOject(new Bullet(player.getPolygon().getCenterX(), player.getPolygon().getCenterY(), player.angle + chance, life, true));
			}
			
		} 
		timer.update();
	}

	@Override
	public void draw(Graphics g) {
		
	}

}
