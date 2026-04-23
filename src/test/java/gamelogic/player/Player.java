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


public class Player extends PhysicsObject{
	public float walkSpeed = 400;
	public float jumpPower = 1350;
	public long started = System.currentTimeMillis();
	public Information myInfo;
	public Key hasKey = null;

	private Level holder;
	private boolean isJumping = false;
	private boolean neg = false;
	private int[] colors = {255, 255, 0};

	public Player(float x, float y, Level level) {
	
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
		holder = level;

		int r = (int)(Math.random()*255);
		int g = (int)(Math.random()*255);
		int b = (int)(Math.random()*255);
		colors[0] = r;
		colors[1] = g;
		colors[2] = b;

		myInfo = new Information((Float)super.getX(), (Float)super.getY(), width-offset, height-offset, hasKey, colors);
	}

	@Override
	public void update(float tslf) {
		super.update(tslf);
		
		movementVector.x = 0;
		if(PlayerInput.isLeftKeyDown()) {
			if (neg){
				movementVector.x = +walkSpeed;
			} else {
				movementVector.x = -walkSpeed;
			}
		}
		if(PlayerInput.isRightKeyDown()) {
			if (neg){
				movementVector.x = -walkSpeed;
			} else {
				movementVector.x = +walkSpeed;
			}
		}
		if(PlayerInput.isJumpKeyDown() && !isJumping) {
			movementVector.y = -jumpPower;
			isJumping = true;
		}

		// Checks if the Q key is down and then teleports if it has been more than 1 second since last teleport.
		// if(PlayerInput.isQKeyDown()) {
			
		// 	if (System.currentTimeMillis()-started >= 1000){
		// 		int h = holder.getLevelData().getMapdata().getHeight();
		// 		int w = holder.getLevelData().getMapdata().getWidth();
		// 		boolean moved = false;
		// 		while (moved == false){
		// 			int col = (int) (Math.random() * (h));
		// 			int row = (int) (Math.random() * (w));
		// 			if (row+1 < holder.getMap().getTiles()[col].length && 
		// 					col < holder.getMap().getTiles().length && 
		// 					!holder.getMap().getTiles()[col][row].isSolid() && 
		// 					holder.getMap().getTiles()[col][row+1].isSolid()){
		// 				Vector2D newVec = new Vector2D((col*holder.getLevelData().getMapdata().getTileSize()), row*holder.getLevelData().getMapdata().getTileSize());
		// 				this.setPosition(newVec);
		// 				int offset =(int)(holder.getLevelData().getTileSize()*0.1);
		// 				this.hitbox = new RectHitbox(this, offset, offset, width -offset, height - offset);
		// 				// hitbox.setPosition(newVec);
		// 				movementVector = new Vector2D(0, 0);
		// 				started = System.currentTimeMillis();
		// 				this.continueThis();
		// 				moved = true;
		// 			}
		// 		}
		// 	}
		// }

		

		isJumping = true;
		if(collisionMatrix[BOT] != null) isJumping = false;
		myInfo.changeInfo((Float)super.getX(), (Float)super.getY(), (int)hitbox.getX(), (int)hitbox.getY(), hasKey, colors);
	}

	public void switchMovement(boolean toWhat){
		neg = toWhat;
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

	// Prints out debugging content.
	public void continueThis(){
		System.out.println("S  " + this.getX() + "  " + this.getY());
		System.out.println("H  " + hitbox.getX() + "  " + hitbox.getY());
	}

	public Key getKey() {
		return hasKey;
	}

	
}
