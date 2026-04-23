package gamelogic.tiles;

import java.awt.image.BufferedImage;

import gameengine.hitbox.RectHitbox;
import gamelogic.level.Level;

public class Door extends Tile{

	public Door(float x, float y, int size, BufferedImage image, Level level) {
		super(x, y, size, image, false, level);
		hitbox = new RectHitbox(x*size, y*size, 30, 0, size-30, size);
	}

	@Override
	public void update(float tslf) {
		if((level.getKeyWin()==(level.getPlayer().hasKey!=null))){
			
		}
		if(hitbox.isIntersecting(level.player.getHitbox())&&(level.getKeyWin()==(level.getPlayer().hasKey!=null))) level.onPlayerWin();
	}
	
	public void setImage(BufferedImage image) {
		setImage(image);
	}
}
