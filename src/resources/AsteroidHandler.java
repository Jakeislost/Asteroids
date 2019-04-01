package resources;

import java.util.Random;

import org.newdawn.slick.geom.Polygon;

import objects.Asteroid;
import objects.Asteroid.AsteroidType;
import objects.GameObject;
import objects.Handler;
import objects.UFO;
import states.Game;

public class AsteroidHandler {

	private Handler handler; // handler responsible for managing objects
	private Random rand = new Random(); // random variable
	
	//delay before new asteroids
	private int asteroidDelay, asteroidTimer = 500;
	private int UFODelay, UFOTimer = rand.nextInt(500) + 2000;
	
	public AsteroidHandler(Handler handler) {
		this.handler = handler;
		
		//add in the starting asteroids at a random position
		//they start above the screen in order to avoid it spawning on the player
		for(int i = 0; i < 4; i++) {
			handler.addOject(new Asteroid(rand.nextInt(Game.GAME_END_X - Game.GAME_START_X), 25, AsteroidType.large, (float) Math.toRadians(rand.nextInt(360))));
		}
	}	
	
	public void update() {
		if(asteroidDelay < asteroidTimer) asteroidDelay++;
		else {
			for(int i = 0; i < 2; i++) {
				handler.addOject(new Asteroid(rand.nextInt(Game.GAME_END_X - Game.GAME_START_X), 25, AsteroidType.large, (float) Math.toRadians(rand.nextInt(360))));
			}
			asteroidDelay = 0;
		}
		if(UFODelay < UFOTimer) UFODelay++;
		else {
			UFODelay = 0;
			handler.addOject(new UFO());
		}
		
		/*
		 * in order to ensure there is always a challenge
		 * for the player,
		 * each asteroid is given a score, and if the total score is
		 * too low, more asteroids are spawned
		 * 
		 * Bigger asteroids have a high score of 12 due to the
		 * potential of spawning 6 smallest asteroids
		 * therefore (6 * 2 = 12)
		 * or 3 medum asteroids
		 * therefore (3 * 4 = 12)
		 */
		int count = 0;
		for(GameObject o : handler.getObjectList()) {
			if(o instanceof Asteroid) {
				switch(((Asteroid) o).getType()) {
					case large :
						count += 12;
						break;
					case medium :
						count += 4;
						break;
					case small :
						count += 2;
						break;
				}
			}
		}
		if(count < 2 * 12) {
			for(int i = 0; i < 2; i++) {
				handler.addOject(new Asteroid(rand.nextInt(Game.GAME_END_X - Game.GAME_START_X), 25, AsteroidType.large, (float) Math.toRadians(rand.nextInt(360))));
			}
		}
	}
	
	/*
	 * this function handes the creation of the asteroid shapes
	 * each type has two variants for the sake of variance
	 * in looks.
	 * this also handles the speed, bigger asteroids move slower
	 * and vice versa for smal asteroids
	 */
	public static Polygon createAsteroid(Asteroid a) {
		Polygon Asteroid = new Polygon();
		Random rand = new Random();
		
		Asteroid = new Polygon();
		switch(a.getType()) {
			case large :
				if(rand.nextInt(2) == 0) {
					Asteroid.addPoint(40, 40);
					Asteroid.addPoint(60, 43);
					Asteroid.addPoint(60, 20);
					Asteroid.addPoint(90, 10);
					Asteroid.addPoint(100, 20);
					Asteroid.addPoint(130, 35);
					Asteroid.addPoint(120, 50);
					Asteroid.addPoint(130, 60);
					Asteroid.addPoint(100, 90);
					Asteroid.addPoint(80, 100);
					Asteroid.addPoint(50, 80);
				} else {
					Asteroid.addPoint(40, 40);
					Asteroid.addPoint(50, 45);
					Asteroid.addPoint(55, 42);
					Asteroid.addPoint(70, 20);
					Asteroid.addPoint(90, 25);
					Asteroid.addPoint(120, 40);
					Asteroid.addPoint(125, 50);
					Asteroid.addPoint(110, 55);
					Asteroid.addPoint(120, 80);
					Asteroid.addPoint(105, 92);
					Asteroid.addPoint(90, 90);
					Asteroid.addPoint(60, 110);
					Asteroid.addPoint(50, 100);
					Asteroid.addPoint(30, 70);
				}
				
				
				a.speed = 1;
				break;
			case medium :
				if(rand.nextInt(2) == 0) { 
					Asteroid.addPoint(20, 20);
					Asteroid.addPoint(30, 10);
					Asteroid.addPoint(50, 5);
					Asteroid.addPoint(70, 15);
					Asteroid.addPoint(65, 30);
					Asteroid.addPoint(70, 40);
					Asteroid.addPoint(60, 50);
					Asteroid.addPoint(30, 40);
				} else {
					Asteroid.addPoint(20, 20);
					Asteroid.addPoint(30, 25);
					Asteroid.addPoint(50, 15);
					Asteroid.addPoint(70, 30);
					Asteroid.addPoint(75, 50);
					Asteroid.addPoint(60, 55);
					Asteroid.addPoint(55, 60);
					Asteroid.addPoint(40, 50);
					Asteroid.addPoint(35, 55);
					Asteroid.addPoint(15, 40);
				}
				a.speed = 3;
				break;
			case small :
				if(rand.nextInt(2) == 0) { 
					Asteroid.addPoint(20, 20);
					Asteroid.addPoint(25, 15);
					Asteroid.addPoint(40, 20);
					Asteroid.addPoint(45, 30);
					Asteroid.addPoint(35, 35);
					Asteroid.addPoint(25, 30);
				} else {
					Asteroid.addPoint(20, 17);
					Asteroid.addPoint(25, 12);
					Asteroid.addPoint(30, 10);
					Asteroid.addPoint(35, 12);
					Asteroid.addPoint(39, 15);
					Asteroid.addPoint(35, 20);
					Asteroid.addPoint(40, 23);
					Asteroid.addPoint(30, 28);
					Asteroid.addPoint(26, 21);
					
				}
				
				a.speed = 3.5f;
				break;
		}
		Asteroid.setCenterX(a.getX());
		Asteroid.setCenterY(a.getY());
		
		
		return Asteroid;
	}
	
}
