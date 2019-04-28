package weapons.objects;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.StateBasedGame;

import objects.Asteroid;
import objects.GameObject;
import resources.FrameTimer;
import states.Game;

public class Bullet extends GameObject{

	private float speed = 10, UFOSpeed = 5, // Speed of the bullet 
					angle; // angle the bullet is moving towards
	private Polygon bullet; // bullet shape (Triangle)
	
	private FrameTimer life;
	private boolean hit; // in order to stop concurrent modification error
	
	private boolean friendly;
	
	public Bullet(float x, float y, float angle, int life, boolean friendly) {
		super(x, y);
		this.angle = angle;
		this.life = new FrameTimer(life, false);
		
		this.friendly = friendly;
		
		// define bullet
		bullet = new Polygon();
		bullet.addPoint(2, 0);
		bullet.addPoint(4, 4);
		bullet.addPoint(0, 4);
		//setup bullets co-ord's
		if(!friendly) bullet = (Polygon) bullet.transform(Transform.createScaleTransform(3, 3));
		bullet.setCenterX(x);
		bullet.setCenterY(y);
		//set rotation
		bullet = (Polygon) bullet.transform(Transform.createRotateTransform(angle, x, y));
		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta, ArrayList<GameObject> object) {
		if(!hit) {
			life.update(); // increase life frames
	        if(life.isDone()) object.remove(this);
	        //move bullet;
			this.bullet = (Polygon)this.bullet.transform(Transform.createTranslateTransform((float)(((friendly) ? speed : UFOSpeed)*Math.sin(angle)),(float)(((friendly) ? speed : UFOSpeed)*Math.cos(angle)*-1)));
	        updateBullet(); // loop round the sides
	        
	        //collision
	        for(GameObject o : object) {
	        	if(friendly && o instanceof Asteroid) {
	        		if(((Asteroid)o).getPolygon().contains(bullet.getCenterX(), bullet.getCenterY()) && !((Asteroid) o).getHit()) {
	        			((Asteroid) o).destroy(object, false);
	        			hit = true;
	        			break;
	        		}
	        	}
	        }
		} else object.remove(this);
		
	}

	@Override
	public void draw(Graphics g) {
		//clip to enable clean graphics
		g.setClip(Game.GAME_START_X, Game.GAME_START_Y, Game.GAME_END_X - Game.GAME_START_X, Game.GAME_END_Y - Game.GAME_START_Y);
		if(!hit) {
			g.setColor((friendly) ? Color.white : Color.red);
			g.draw(bullet);
		}
		g.clearClip();
	}

	@Override
	public void input(Input IM) {
		
	}
	
	private void updateBullet()
    {
		if(bullet.getCenterX() > Game.GAME_END_X){
            this.bullet.setCenterX(Game.GAME_START_X);
        }
        else if(bullet.getCenterX() < Game.GAME_START_X)
        {
            this.bullet.setCenterX(Game.GAME_END_X);
        }
        if(bullet.getCenterY() < Game.GAME_START_Y)
        {
            this.bullet.setCenterY(Game.GAME_END_Y);
        }
        else if(bullet.getCenterY() > Game.GAME_END_Y)
        {
            this.bullet.setCenterY(Game.GAME_START_Y);
        }          
    }
	
	public boolean getType() {
		return friendly;
	}
	
	public Polygon getPolygon() {
		return bullet;
	}

}
