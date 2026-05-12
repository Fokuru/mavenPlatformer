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

public class OtherPlayers {

    private float x, y;
    private int id = -1;
    private int[] colors;
    Level level;
    private Key hasKey = null;

    public OtherPlayers(float x, float y, Level newLevel, int[] newColors) {
        this.x = x;
        this.y = y;
        this.colors = newColors;
        level = newLevel;  
    }

    public void draw(Graphics g) {
        g.setColor(new Color(colors[0], colors[1], colors[2]));
        MyGraphics.fillRectWithOutline(g, (int)x, (int)y, 
                                       level.getLevelData().getTileSize(),
                                       level.getLevelData().getTileSize());
    }

    public void changeX(Float x, float tslf) {
        this.x = x;
    }

    public void changeY(Float y, float tslf) {
        this.y = y;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void changeKey(Key key) {
        hasKey = key;
    }

    public Key getKey() {
        return hasKey;
    }
}
