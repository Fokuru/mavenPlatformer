package gamelogic.level;

import java.lang.reflect.Array;

import gameengine.loaders.Mapdata;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import gameengine.*;

public class LevelData {

	private Mapdata mapdata;
	private int playerX;
	private int playerY;
	// private ArrayList<Integer> otherPX;
	// private ArrayList<Integer> otherPY;
	
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
