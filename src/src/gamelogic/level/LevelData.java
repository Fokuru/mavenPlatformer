package gamelogic.level;

import gameengine.loaders.Mapdata;

public class LevelData {

	private Mapdata mapdata;
	private int playerX;
	private int playerY;
	
	public LevelData(Mapdata mapdata, int playerX, int playerY) {
		this.mapdata = mapdata;
		this.playerX = playerX;
		this.playerY = playerY;
	}
	
	//-----------------------------Getters
	public Mapdata getMapdata() {
		return mapdata;
	}
	
	public int getPlayerX() {
		return playerX;
	}
	
	public int getPlayerY() {
		return playerY;
	}

	public int getTileSize(){
		return mapdata.getTileSize();
	}

}
