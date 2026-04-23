package gamelogic.tiles;

import java.awt.image.BufferedImage;

import gameengine.hitbox.RectHitbox;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.level.Level;

public class Button extends Tile {

    private boolean pressed;
	public Button(float x, float y, int size, BufferedImage image, Level level, boolean pressed) {
		super(x, y, size, image, false, level);
		this.pressed = pressed;
		System.out.println("e");
		this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
	}
	
	public boolean getPressed() {
		return pressed;
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
		Tileset tileset = GameResources.tileset;
        if (pressed){
			level.removeSpikes();
            setImage(tileset.getImage("Button_down"));
        } else {
            setImage(tileset.getImage("Button_up"));
        }
	}

	
	public void update(float tslf) {
		if (hitbox.isIntersecting(level.player.getHitbox()))
				setPressed(true);
		} 
}