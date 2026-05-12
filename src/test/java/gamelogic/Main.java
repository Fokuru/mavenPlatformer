package gamelogic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import gameengine.GameBase;
import gameengine.input.KeyboardInputManager;
import gameengine.loaders.LeveldataLoader;
import gamelogic.clientHandling.Information;
import gamelogic.level.Level;
import gamelogic.level.LevelData;
import gamelogic.level.PlayerDieListener;
import gamelogic.level.PlayerWinListener;
import gamelogic.player.Player;

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
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;


	
	public static void main(String[] args) {
		Main main = new Main();
		main.start("Eden Jump", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public Main(){
		try {
			// get the localhost IP address; if the server runs elsewhere, use that IP
			InetAddress host = InetAddress.getLocalHost();
			socket = new Socket(host.getHostName(), 9877);
			// write to socket using ObjectOutputStream
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (Exception w) {
			System.out.println(w);
		}
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
		new ClientStuff(ois, oos).start();

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
			currentLevel.getPlayer().myInfo.setWon(false);
			currentLevel.getPlayer().myInfo.setButtonPressed(false); 
			currentLevel.buttonAlreadyActivated = false;
		}
	}

	@Override
	public void update(float tslf) {
		if (KeyboardInputManager.isKeyDown(KeyEvent.VK_N)) init();
		if (KeyboardInputManager.isKeyDown(KeyEvent.VK_ESCAPE)) System.exit(0);

		if (active) {
			currentLevel.update(tslf);
		}

		Information info = currentLevel.getPlayer().myInfo;
		Player p = currentLevel.getPlayer();
		info.setLevelIndex(currentLevelIndex);

		boolean shouldSend =
			currentLevel.isActive() ||
			(info.hasWon() && !p.hasSentWin);

		if (shouldSend) {
			try {
				oos.reset();
				oos.writeObject(info);
				oos.flush();

				if (info.hasWon()) {
					p.hasSentWin = true;
				}

			} catch (IOException e) {
				System.out.println("Failed to send player info: " + e.getMessage());
			}
		}

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
		private final ObjectInputStream ois;
		@SuppressWarnings("unused")
		private final ObjectOutputStream oos;

		public ClientStuff(ObjectInputStream i, ObjectOutputStream o){
			this.ois = i;
			this.oos = o;
		}

		@Override
		public void run(){
			while (true) {
				try {
					Information[] others = (Information[]) ois.readObject();
					System.out.println("Received data for " + others.length + " other players");
					for (Information info : others) {
						System.out.println("  client got id=" + info.getId() +
										" x=" + info.getMyX() +
										" y=" + info.getMyY());
					}

					for (Information info : others) {
						if (info != null &&
							info.hasWon() &&
							info.getLevelIndex() == currentLevelIndex &&  
							!currentLevel.isPlayerWin()) {                 
							currentLevel.onPlayerWin();
							break; // no need to check more
						}
					}

					for (Information info : others) {
						if (info != null &&
						info.isButtonPressed() &&
						currentLevelIndex == info.getLevelIndex() &&
						!currentLevel.buttonAlreadyActivated) {

						currentLevel.removeSpikes();
						currentLevel.buttonAlreadyActivated = true;
					}

					}

					currentLevel.updateOthers(others, 0);

				} catch (EOFException e) {
					System.out.println("Server closed the connection: " + e.getMessage());
					break;
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

}
