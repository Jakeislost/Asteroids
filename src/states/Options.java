package states;

import java.awt.Font;

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

public class Options extends BasicGameState{
	
	/*
	 * Options menu which dictates a lot of window based stuff
	 * 
	 * FPS
	 * VSYNC
	 * 
	 */

	private Input IM;
	private UI ui;
	
	private TrueTypeFont font;
	
	@Override
	public void init(GameContainer gc, StateBasedGame gsm) throws SlickException {
		IM = gc.getInput();
		IM.addMouseListener(gsm);
		
		ui = new UI();
		ui.addList("Main Menu", 200, 400, 300, 50, 5);
		ui.getList("Main Menu").addButton("Vsync");
		ui.getList("Main Menu").addButton("Show FPS");
		ui.getList("Main Menu").addButton("Sound");
		ui.getList("Main Menu").addButton("Back");
		
		font = new TrueTypeFont(new Font("Times New Roman", Font.BOLD, 40), true);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame gsm, Graphics g) throws SlickException {
		ui.draw(g);
		g.setColor(Color.cyan);
		g.setFont(font);
		g.drawString("Options", 350 - (g.getFont().getWidth("Options") / 2), 200);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta) throws SlickException {
		//Mouse updates
		mouse(gc, gsm);
		ui.getList("Main Menu").getButton("Vsync").changeColours((gc.isVSyncRequested()) ? Color.green : Color.red, Color.black);
		ui.getList("Main Menu").getButton("Show FPS").changeColours((gc.isShowingFPS()) ? Color.green : Color.red, Color.black);
		ui.getList("Main Menu").getButton("Sound").changeColours((gc.isSoundOn()) ? Color.green : Color.red, Color.black);
	}
	
	public void mouse(GameContainer gc, StateBasedGame gsm) {
		Point loc = new Point(IM.getMouseX(), IM.getMouseY());
		ui.mouseOver(loc);
		if(ui.getList("Main Menu").isPressed("Vsync", loc, IM)) {
			gc.setVSync((gc.isVSyncRequested()) ? false : true);
		} else if(ui.getList("Main Menu").isPressed("Show FPS", loc, IM)) {
			gc.setShowFPS((gc.isShowingFPS()) ? false : true);
		} else if(ui.getList("Main Menu").isPressed("Sound", loc, IM)) {
			gc.setSoundOn((gc.isSoundOn()) ? false : true);
		} else if(ui.getList("Main Menu").isPressed("Back", loc, IM)) {
			gsm.enterState(0);
		}
		
	}

	@Override
	public int getID() {
		return 4;
	}

}
