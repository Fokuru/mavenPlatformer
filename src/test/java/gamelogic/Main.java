package gamelogic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import gameengine.GameBase;
import gameengine.graphics.MyWindow;
import gameengine.input.KeyboardInputManager;
import gameengine.loaders.LeveldataLoader;
import gamelogic.clientHandling.Information;
import gamelogic.clientHandling.Server;
import gamelogic.level.Level;
import gamelogic.level.LevelData;
import gamelogic.level.PlayerDieListener;
import gamelogic.level.PlayerWinListener;

public class Main extends GameBase implements PlayerDieListener, PlayerWinListener, ScreenTransitionListener{
	public static final int SCREEN_WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()-200;
	public static final int SCREEN_HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-200;
	public static final boolean DEBUGGING = false;

	private ScreenTransition screenTransition = new ScreenTransition();

	private LevelData[] levels;
	private ArrayList<Level> currentLevel;
	private int currentLevelIndex;
	private boolean active;
	
	private int numberOfTries;
	private long levelStartTime;
	private long levelFinishTime;
	private static Server server;
	
	
	private LevelCompleteBar levelCompleteBar;

	public static void main(String[] args) {
		server = new Server();
	}

	@Override
	public void init() {

		new Thread(() -> {
            while (server.getConnections() > 0) {

		GameResources.load();

		currentLevelIndex = 0;

			levels = new LevelData[4];
			try {
				levels[0] = LeveldataLoader.loadLeveldata("/workspaces/platformer/workspace/maps/firstLevel.txt");
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < server.getConnections(); i++) {
				currentLevel.set(i, new Level(levels[currentLevelIndex]));
				currentLevel.get(i).addPlayerDieListener(this);
				currentLevel.get(i).addPlayerWinListener(this);
			}
			
			
			

			screenTransition.addScreenTransitionListener(this);
			
			active = true;
			
			numberOfTries = 0;
			levelStartTime = System.currentTimeMillis();
			
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.get(0).getPlayer());

		}
		}).start();
	
	}
	
	//-----------------------------------------------------Screen Transition Listener
	@Override
	public void onTransitionActivationFinished() {
		for (int i = 0; i < currentLevel.size(); i++) {
			if(currentLevel.get(i).isPlayerDead()) {
				currentLevel.get(i).restartLevel();
				levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.get(0).getPlayer());
			}
			if(currentLevel.get(i).isPlayerWin()) {
				if(currentLevelIndex < levels.length-1) {
					changeLevel();
				}
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
			currentLevel.get(0).restartLevel();
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.get(0).getPlayer());
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
			for (int i = 0; i < currentLevel.size(); i++) {
				currentLevel.set(i, new Level(levels[currentLevelIndex]));
				currentLevel.get(i).addPlayerDieListener(this);
				currentLevel.get(i).addPlayerWinListener(this);
			}
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.get(0).getPlayer());
		}
	}

	@Override
	public void update(float tslf) {
		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_N)) init();
		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_ESCAPE)) System.exit(0);

		new Thread(() -> {
            while (server.getConnections() > 0) {
				
				if (active) {
					for (int i = 0; i < currentLevel.size(); i++) {
						currentLevel.get(i).update(tslf);
					}
				}

				screenTransition.update(tslf);
				if (levelCompleteBar != null) {
				levelCompleteBar.update(tslf);
				}
			}
		}).start();
	}

	@Override
	public void draw(Graphics g) {

		new Thread(() -> {
            while (server.getConnections() > 0) {
		
			drawBackground(g);
			//Camera-translate
			currentLevel.get(0).draw(g);
			//- Camera-translate
			
			levelCompleteBar.draw(g);
			
			screenTransition.draw(g);
			}
		}).start();
	}

	public void drawBackground(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0-MyWindow.getInsetY(), SCREEN_WIDTH, SCREEN_HEIGHT+MyWindow.getInsetY()*2);
	}

	

	
}
