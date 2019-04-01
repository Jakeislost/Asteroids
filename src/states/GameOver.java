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

public class GameOver extends BasicGameState{

	private Input IM;
	private UI ui;
	private TrueTypeFont font = new TrueTypeFont(new Font("verdana", Font.PLAIN, 40), false);
	
	@Override
	public void init(GameContainer gc, StateBasedGame gsm) throws SlickException {
		IM = gc.getInput();
		IM.addMouseListener(gsm);
		
		ui = new UI();
		ui.addList("Main Menu", 200, 400, 300, 50, 5);
		ui.getList("Main Menu").addButton("Play Again");
		ui.getList("Main Menu").addButton("Main Menu");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame gsm, Graphics g) throws SlickException {
		g.setColor(Color.yellow);
		g.setFont(font);
		g.drawString("Well done! you Scored:", 350 - (g.getFont().getWidth("Well done! you Scored:") / 2), 200);
		g.drawString("" + Game.score, 350 - (g.getFont().getWidth("" + Game.score) / 2), 250);
		ui.draw(g);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame gsm, int delta) throws SlickException {
		
		//Mouse updates
		mouse(gc, gsm);
	}
	
	public void mouse(GameContainer gc, StateBasedGame gsm) {
		Point loc = new Point(IM.getMouseX(), IM.getMouseY());
		ui.mouseOver(loc);
		if(ui.getList("Main Menu").isPressed("Main Menu", loc, IM)) {
			Game.score = 0;
			gsm.enterState(0);
		} else if(ui.getList("Main Menu").isPressed("Play Again", loc, IM)) {
			Game.score = 0;
			gsm.enterState(1);
		}
		
		
	}


	@Override
	public int getID() {
		return 2;
	}

}
