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

import resources.UI;

public class Paused extends BasicGameState{

	private Input IM;
	private UI ui;
	
	private TrueTypeFont font;
	
	@Override
	public void init(GameContainer gc, StateBasedGame gsm) throws SlickException {
		IM = gc.getInput();
		IM.addMouseListener(gsm);
		IM.addKeyListener(gsm);
		
		ui = new UI();
		ui.addList("Main Menu", 200, 400, 300, 50, 5);
		ui.getList("Main Menu").addButton("Resume");
		ui.getList("Main Menu").addButton("Quit");
		
		font = new TrueTypeFont(new Font("Times New Roman", Font.BOLD, 40), true);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame gsm, Graphics g) throws SlickException {
		ui.draw(g);
		
		g.setColor(Color.cyan);
		g.setFont(font);
		g.drawString("Paused", 350 - (g.getFont().getWidth("Paused") / 2), 200);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta) throws SlickException {
		
		//Mouse updates
		mouse(gc, gsm);
	}
	
	public void mouse(GameContainer gc, StateBasedGame gsm) {
		Point loc = new Point(IM.getMouseX(), IM.getMouseY());
		ui.mouseOver(loc);
		if(ui.getList("Main Menu").isPressed("Quit", loc, IM)) {
			((Game)gsm.getState(1)).reset(gc, gsm);
			Game.score = 0;
			gsm.enterState(0);
		} else if(ui.getList("Main Menu").isPressed("Resume", loc, IM)) {
			IM.clearKeyPressedRecord();
			gsm.enterState(1);
		}
		if(IM.isKeyPressed(IM.KEY_ESCAPE)) gsm.enterState(1);
		
	}

	@Override
	public int getID() {
		return 3;
	}

}
