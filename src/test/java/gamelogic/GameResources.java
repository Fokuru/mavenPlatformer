package gamelogic;

import java.awt.image.BufferedImage;

import gameengine.loaders.ImageLoader;
import gameengine.loaders.Tileset;
import gameengine.loaders.TilesetLoader;

public final class GameResources {

	public static Tileset tileset;
	
	public static BufferedImage enemy;
	
	public static void load() {
		try {
			tileset = TilesetLoader.loadTileset("/workspaces/mavenPlatformer/src/test/java/gfx/tileset.txt", ImageLoader.loadImage("/workspaces/mavenPlatformer/src/test/java/gfx/tileset.png"));
			
			enemy = ImageLoader.loadImage("/workspaces/mavenPlatformer/src/test/java/gfx/Enemy.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
