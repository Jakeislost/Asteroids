package states;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import handlers.AsteroidHandler;
import handlers.Handler;
import objects.GameObject;
import objects.Player;
import objects.UFO;
import resources.Util;
import weapons.abilities.Pulse;
import weapons.guns.Blaster;

public class Game extends BasicGameState{
	
	private Handler handler;
	private Input IM;
	private AsteroidHandler AH;
	
	public static int GAME_START_X = 10, GAME_END_X = 690, GAME_START_Y = 10, GAME_END_Y = 630;
	public static int score = 0;
	
	private TrueTypeFont font = new TrueTypeFont(new Font("verdana", Font.BOLD, 30), true);
	
	private ParticleSystem pSys;
	private ConfigurableEmitter emitter;
	
	@Override
	public void init(GameContainer gc, StateBasedGame gsm) throws SlickException {
		reset(gc, gsm);
		try {
			Image particle = Util.getImage("particle");
			pSys = new ParticleSystem(particle, 1000);
			
			emitter = Util.getEmitter("stars").duplicate();
			emitter.setPosition(350,350);
			pSys.addEmitter(emitter);
		} catch(Exception e) {
			e.printStackTrace();
		}
		pSys.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame gsm, Graphics g) throws SlickException {
		g.setColor(Color.black);
		g.fillRect(0, 0, gc.getWidth(), gc.getHeight());
		g.setColor(Color.white);
		g.drawRect(GAME_START_X, GAME_START_Y, GAME_END_X - GAME_START_X, GAME_END_Y - GAME_START_Y);
		
		g.setClip(Game.GAME_START_X, Game.GAME_START_Y, Game.GAME_END_X - Game.GAME_START_X, Game.GAME_END_Y - Game.GAME_START_Y);
		pSys.render();
		g.clearClip();
		handler.draw(g);
		
		g.setColor(Color.yellow);
		g.setFont(font);
		g.drawString("Score: " + score, 400, 660);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta) throws SlickException {
		handler.input(IM);
		if(IM.isKeyPressed(IM.KEY_ESCAPE)) gsm.enterState(3);
		handler.update(gc, gsm, delta);
		
		AH.update();
		pSys.update(delta);
	}
	
	public void reset(GameContainer gc, StateBasedGame gsm) {
		
		if(handler != null) {
			for(GameObject o : handler.getObjectList()) {
				if(o instanceof UFO) {
					((UFO)o).dispose();
				}
			}
		}
		
		handler = new Handler();
		AH = new AsteroidHandler(handler);
		IM = gc.getInput();
		
		score = 0;
		
		IM.addKeyListener(gsm);
		IM.addMouseListener(gsm);
		handler.addOject(new Player(350, 350, 0, new Blaster(handler), new Pulse(handler)));
	}

	@Override
	public int getID() {
		return 1;
	}

}
