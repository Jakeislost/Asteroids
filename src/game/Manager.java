package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import states.Game;
import states.GameOver;
import states.Main;
import states.Options;
import states.Paused;

public class Manager extends StateBasedGame{

	public Manager(String name) {
		super(name);
		
	}
	
	public static void main(String[] args) {
		//setup game container
		try {
			AppGameContainer game = new AppGameContainer(new Manager("Asteroids"));
			game.setTargetFrameRate(60);
			game.setMaximumLogicUpdateInterval(60);
			game.setVSync(true);
			game.setShowFPS(false);
			game.setDisplayMode(700, 700, false);
			game.setAlwaysRender(true);
			game.setIcons(new String[] {"res/images/icon16.png", "res/images/icon32.png"});
			game.start();
			
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
		//load states into the game
		addState(new Main()); // main menu
		addState(new Game()); // gameplay
		addState(new GameOver());
		addState(new Paused()); 
		addState(new Options());
	}

}
