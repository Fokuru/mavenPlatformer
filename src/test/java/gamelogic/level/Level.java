package gamelogic.level;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.clientHandling.Information;
import gamelogic.key.Key;
import gamelogic.player.OtherPlayers;
import gamelogic.player.Player;
import gamelogic.tiles.Button;
import gamelogic.tiles.Door;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;

public class Level implements Serializable {

	private LevelData leveldata;
	private gamelogic.tiledMap.Map map;
	private Key[] key;
	public Player player;
	public Map<Integer, OtherPlayers> otherPlayers;
	private Camera camera;
	private boolean keyWin=false;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;
	private ArrayList<Key> keys = new ArrayList<>();

	private ArrayList<Integer> spikesList = new ArrayList<>();
	public ArrayList<Integer> doorList = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;
	private Tile[][] tiles;

	public boolean buttonAlreadyActivated = false;

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
		otherPlayers = new HashMap<>();

        

	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		System.out.println("Level begun creation");
		int[][] values = mapdata.getValues();
		tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			buttonAlreadyActivated = false;
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0){
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize,tileset.getImage("Air"), false, this); // Air
				}else if (values[x][y] == 1){
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				}else if (values[x][y] == 2){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
					spikesList.add(x);
					spikesList.add(y);
				}else if (values[x][y] == 3){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
					spikesList.add(x);
					spikesList.add(y);
				}else if (values[x][y] == 4){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
					spikesList.add(x);
					spikesList.add(y);
				}else if (values[x][y] == 5){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
					spikesList.add(x);
					spikesList.add(y);
				
				}else if (values[x][y] == 9){
					tiles[x][y] = new Door(xPosition, yPosition, tileSize, tileset.getImage("Door_closed"), this);
					doorList.add(x);
					doorList.add(y);
				}else if (values[x][y] == 10) {
					keys.add(new Key(xPosition*tileSize, yPosition*tileSize, null));
					keyWin=true;
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize,tileset.getImage("Air"), false, this);
				}else if (values[x][y] == 15){
					tiles[x][y] = new Button(xPosition, yPosition, tileSize, tileset.getImage("Button_up"), this, false);
				}

			}
			map = new gamelogic.tiledMap.Map(width, height, tileSize, tiles);
			camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
			
			player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
					this);
			camera.setFocusedObject(player);
			player.myInfo.setWon(false);

			active = true;
			playerDead = false;
			playerWin = false;
			player.hasSentWin = false;
			player.myInfo.setButtonPressed(false);
			
		}

		key = new Key[keys.size()];
		for (int i = 0; i < keys.size(); i++) {
			key[i] = new Key(keys.get(i).getX(), keys.get(i).getY(), this);
		}
		System.out.println("Level succesful creation");
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		if (playerWin) return;
		active = false;
		playerWin = true;
		if (player != null && player.myInfo != null) {
        	player.myInfo.setWon(true);
		}
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);
			
			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();
			
			
			for (int i = 0; i < key.length; i++) {
				//System.out.println("Checking for key");
				key[i].update(tslf);
				if (player.getHitbox().isIntersecting(key[i].getHitbox())&&player.hasKey==null) {
					player.hasKey=key[i];
					key[i].pickedUp=true;
					System.out.println("Succesfully picked up");
				}
			}

			

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	
	




	public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 for (int i = 0; i < key.length; i++) {
	   		 key[i].draw(g);
	   	 }

	   	 // Draw the player
	   	 player.draw(g);

		for (OtherPlayers remote : otherPlayers.values()) {
			if (remote != null) {
				remote.draw(g);
			}
		}

	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());
	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public gamelogic.tiledMap.Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}

	public void addPlayer(int id, OtherPlayers newPlayer) {
		otherPlayers.put(id, newPlayer);
	}

	public void handleRemotePlayer(Information info) {
		if (info == null || info.getId() < 0) {
			return;
		}

		OtherPlayers remote = otherPlayers.get(info.getId());
		if (remote == null) {
			remote = new OtherPlayers(info.getMyX(), info.getMyY(), this, info.getColors());
			remote.setId(info.getId());
			otherPlayers.put(info.getId(), remote);
		} else {
			remote.changeX(info.getMyX(), 0);
			remote.changeY(info.getMyY(), 0);
			remote.changeKey(info.getKey());
		}
	}


	public boolean getKeyWin(){
		return keyWin;
	}
	
	public void removeSpikes(){
		while(spikesList.size()!=0){
			tiles[spikesList.get(0)][spikesList.get(1)]= new Tile((float)spikesList.get(0), (float)spikesList.get(1), tileSize, tileset.getImage("Air"), false, this);
			spikesList.remove(0);
			spikesList.remove(0);
		}
		player.myInfo.setButtonPressed(true);
	}

	public void updateOthers (Information[] others, int tslf) {
		for (Information info : others) {
			if (info != null) {
				handleRemotePlayer(info);
			}
		}
	}


}