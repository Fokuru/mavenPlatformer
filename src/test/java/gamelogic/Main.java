package gamelogic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import gameengine.GameBase;
import gameengine.input.KeyboardInputManager;
import gameengine.loaders.LeveldataLoader;
import gamelogic.level.Level;
import gamelogic.level.LevelData;
import gamelogic.level.PlayerDieListener;
import gamelogic.level.PlayerWinListener;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;

public class Main extends GameBase implements PlayerDieListener, PlayerWinListener, ScreenTransitionListener{

	public static final int SCREEN_WIDTH = 1280;
	public static final int SCREEN_HEIGHT = 860;
	public static final boolean DEBUGGING = false;

	private ScreenTransition screenTransition = new ScreenTransition();

	private LevelData[] levels;
	private Level currentLevel;
	private int currentLevelIndex;
	private boolean active;
	
	private int numberOfTries;
	private long levelStartTime;
	private long levelFinishTime;
	
	private LevelCompleteBar levelCompleteBar;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.start("Eden Jump", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public Main(){
		 try {
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        	Socket socket = new Socket(host.getHostName(), 9876);
            //write to socket using ObjectOutputStream
        	ObjectOutputStream   oos = new ObjectOutputStream(socket.getOutputStream());
       
			ObjectInputStream   ois = new ObjectInputStream(socket.getInputStream());
    			
        	} catch (Exception w){
				
			};
	}

	@Override
	public void init() {
		GameResources.load();

		currentLevelIndex = 0;

		levels = new LevelData[3];
		try {
			levels[0] = LeveldataLoader.loadLeveldata("/workspaces/mavenPlatformer/src/test/java/maps/firstLevel.txt");
			levels[1] = LeveldataLoader.loadLeveldata("/workspaces/mavenPlatformer/src/test/java/maps/secondLevel.txt");
			levels[2] = LeveldataLoader.loadLeveldata("/workspaces/mavenPlatformer/src/test/java/maps/thirdLevel.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentLevel = new Level(levels[currentLevelIndex]);

		currentLevel.addPlayerDieListener(this);
		currentLevel.addPlayerWinListener(this);

		screenTransition.addScreenTransitionListener(this);
		
		active = true;
		
		numberOfTries = 0;
		levelStartTime = System.currentTimeMillis();
		
		levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
	}
	
	//-----------------------------------------------------Screen Transition Listener
	@Override
	public void onTransitionActivationFinished() {
		if(currentLevel.isPlayerDead()) {
			currentLevel.restartLevel();
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
		}
		if(currentLevel.isPlayerWin()) {
			if(currentLevelIndex < levels.length-1) {
				changeLevel();
			}
		}
	}

	@Override
	public void onTransitionFinished() {
		active = true;
	}

	//-----------------------------------------------Player Listener
	@Override
	public void onPlayerDeath() {
		numberOfTries++;
		levelStartTime = System.currentTimeMillis();
		if(DEBUGGING) {
			currentLevel.restartLevel();
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
			return;
		}
		screenTransition.showLoseScreen(numberOfTries);
		
		active = false;
	}

	@Override
	public void onPlayerWin() {
		levelFinishTime = System.currentTimeMillis();
		screenTransition.showVictorySceen(levelFinishTime - levelStartTime);
		
		active = false;
	}

	private void changeLevel() {
		numberOfTries = 0;
		if(currentLevelIndex < levels.length-1) {
			currentLevelIndex++;
			currentLevel = new Level(levels[currentLevelIndex]);

			currentLevel.addPlayerDieListener(this);
			currentLevel.addPlayerWinListener(this);
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
		}
	}

	@Override
	public void update(float tslf) {
		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_N)) init();
		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_ESCAPE)) System.exit(0);

		if (active) currentLevel.update(tslf);

		screenTransition.update(tslf);
		
		levelCompleteBar.update(tslf);
	}

	@Override
	public void draw(Graphics g) {
		drawBackground(g);

		//Camera-translate
		currentLevel.draw(g);
		//- Camera-translate
		
		levelCompleteBar.draw(g);
		
		screenTransition.draw(g);
	}

	public void drawBackground(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public class ClientStuff extends Thread{
		ObjectInputStream ois;
		ObjectOutputStream oos;

		public ClientStuff(ObjectInputStream i, ObjectOutputStream o){
			ois=i;
			oos=o;
		}

		public void run(){

		}
	}

}
