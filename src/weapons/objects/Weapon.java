package weapons.objects;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;

import handlers.Handler;
import objects.Player;
import resources.FrameTimer;

public abstract class Weapon {

	//weapon variables
	protected String name, desc;
	protected int life;
	
	//sound
	protected Sound fireSound;
	
	//timers etc
	protected FrameTimer timer;
	protected Handler handler;
	
	//level
	protected int level, levelCap;
	
	protected boolean isActive;
	
	public Weapon(Handler handler) {
		this.handler = handler;
	}
	
	public abstract void fire(Player player, int delta);
	public abstract void draw(Graphics g);
	
	public int getLevel() {
		return level;
	}
	public void setLevelCap(int levelCap) {
		this.levelCap = levelCap;
	}
	public int getLevelCap() {
		return levelCap;
	}
	
	public void incrementLevel() {
		if(level < levelCap)level++;
	}
	public void decreaseLevel() {
		if(level > 0) level--;
	}
	public void resetLevel() {
		level = 0;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getROF() {
		return timer.getLimit();
	}

	public void setROF(int ROF) {
		timer = new FrameTimer(ROF, true);
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}
	
	public int getDelay() {
		return timer.getFrameCount();
	}
	public void completeTimer() {
		timer.completeTimer();
	}
	
	public boolean isActive() {
		return isActive;
	}
	
}
