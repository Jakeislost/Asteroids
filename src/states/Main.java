package states;

import java.awt.Font;
import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import objects.Handler;
import objects.Player;
import resources.UI;
import resources.Util;

public class Main extends BasicGameState{
	
	private Input IM;
	private UI ui;
	
	private TrueTypeFont font;
	
	private Handler handler;
	
	@Override
	public void init(GameContainer gc, StateBasedGame gsm) throws SlickException {
		IM = gc.getInput();
		IM.addMouseListener(gsm);
		IM.addKeyListener(gsm);
		
		handler = new Handler();
		//handler.addOject(new Player(100, 100, 0));
		
		ui = new UI();
		ui.addList("Main Menu", 200, 400, 300, 50, 5);
		ui.getList("Main Menu").addButton("Play");
		ui.getList("Main Menu").addButton("Options");
		ui.getList("Main Menu").addButton("Quit");
		
		font = new TrueTypeFont(new Font("Times New Roman", Font.BOLD, 40), true);
		
		//load stuff
		
		try {
			Util.addObject("res/images/particle.png", "particle");
			Util.addObject("res/xml/ExplosionSmall.xml", "explosionSmall");
			Util.addObject("res/xml/ExplosionMedium.xml", "explosionMedium");
			Util.addObject("res/xml/ExplosionLarge.xml", "explosionLarge");
			Util.addObject("res/xml/powerup.xml", "powerup");
			Util.addObject("res/xml/Rocket.xml", "rocket");
			Util.addObject("res/xml/Stars.xml", "stars");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame gsm, Graphics g) throws SlickException {
		ui.draw(g);
		handler.draw(g);
		g.setColor(Color.cyan);
		g.setFont(font);
		g.drawString("Asteroids", 350 - (g.getFont().getWidth("Asteroids") / 2), 200);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta) throws SlickException {
		handler.update(gc, gsm, delta);
		//Mouse updates
		mouse(gc, gsm);
	}
	
	public void mouse(GameContainer gc, StateBasedGame gsm) {
		Point loc = new Point(IM.getMouseX(), IM.getMouseY());
		ui.mouseOver(loc);
		if(ui.getList("Main Menu").isPressed("Quit", loc, IM)) {
			System.exit(0);
		} else if(ui.getList("Main Menu").isPressed("Options", loc, IM)) {
			gsm.enterState(4);
		} else if(ui.getList("Main Menu").isPressed("Play", loc, IM)) {
			IM.clearKeyPressedRecord();
			gsm.enterState(1);
		}
		
		handler.input(IM);
		
	}

	@Override
	public int getID() {
		return 0;
	}

	

}
