package gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gameengine.maths.Vector2D;
import gamelogic.Main;
import gamelogic.clientHandling.Information;
import gamelogic.level.Level;
import gamelogic.tiles.Tile;
import gamelogic.key.Key;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OtherPlayers extends PhysicsObject {
    private int[] colors = {255, 255, 0};
    private Key hasKey = null;
    public Information myInfo;
    
    public OtherPlayers(float x, float y, Level level, int[] newColors) {
        super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
        colors = newColors;
        int offset =(int)(level.getLevelData().getTileSize()*0.1);
        this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
        myInfo = new Information((Float)super.getX(), (Float)super.getY(), width-offset, height-offset, hasKey, colors);
    }

    @Override
	public void draw(Graphics g) {
		g.setColor(new Color(colors[0], colors[1], colors[2]));
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
		
		if(Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if(t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}
		
		hitbox.draw(g);
	}

    public void changeX (Float x, float tslf) {
        super.newX(x);
        updateCollisionMatrix(tslf);
        int offset =(int)(level.getLevelData().getTileSize()*0.1);
        myInfo = new Information((Float)super.getX(), (Float)super.getY(), width-offset, height-offset, hasKey, colors);
    }

    public void changeY (Float y, float tslf) {
        super.newY(y);
        updateCollisionMatrix(tslf);
        int offset =(int)(level.getLevelData().getTileSize()*0.1);
        myInfo = new Information((Float)super.getX(), (Float)super.getY(), width-offset, height-offset, hasKey, colors);
    }

    public void changeKey (Key key) {
        hasKey = key;
        int offset =(int)(level.getLevelData().getTileSize()*0.1);
        int x = (int) super.getX();
        myInfo = new Information((Float)super.getX(), (Float)super.getY(), width-offset, height-offset, hasKey, colors);
    }
}
