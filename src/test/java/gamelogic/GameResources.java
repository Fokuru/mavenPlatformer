package gamelogic;

import java.awt.image.BufferedImage;

import gameengine.loaders.ImageLoader;
import gameengine.loaders.Tileset;
import gameengine.loaders.TilesetLoader;

public final class GameResources {

	public static Tileset tileset;
	
	public static BufferedImage keyImage;
	
	public static void load() {
		try {
			tileset = TilesetLoader.loadTileset("/workspaces/mavenPlatformer/src/test/java/gfx/picotileset.txt", ImageLoader.loadImage("/workspaces/mavenPlatformer/src/test/java/gfx/TileMapPicoPenv3.png"));
			
			keyImage = ImageLoader.loadImage("/workspaces/mavenPlatformer/src/test/java/gfx/PicoKey.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
